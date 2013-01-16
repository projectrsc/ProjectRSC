package com.prsc.gs;

import java.io.IOException;




import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.AdaptiveReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.prsc.config.Constants;
import com.prsc.gs.connection.RSCConnectionHandler;
import com.prsc.gs.connection.RSCProtocolDecoder;
import com.prsc.gs.connection.RSCProtocolEncoder;
import com.prsc.gs.connection.filter.ConnectionFilter;
import com.prsc.gs.core.GameEngine;
import com.prsc.gs.core.LoginConnector;
import com.prsc.gs.core.TaskManager;
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.event.SingleEvent;
import com.prsc.gs.model.World;
import com.prsc.gs.registrar.PortRegistrar;
import com.prsc.gs.util.Logger;

public final class Server {
	
	private static Server server;

	private Channel channel;

	private LoginConnector connector;

	private GameEngine engine;
	
	private TaskManager taskManager;

	private boolean running;

	private DelayedEvent updateEvent;

	private NioServerSocketChannelFactory factory;
	
	//private NioDatagramChannelFactory udpFactory;
	
	public static void print(String str, boolean newline) {
		System.out.printf("%-32s" + (newline ? "\n" : ""), str);
	}

	public static void main(String[] args) throws IOException {//Registering
		System.out.printf("\t*** ProjectRSC Game Server ***\n\n");
		Constants.GameServer.initConfig("server.conf");
		//Constants.GameServer.initConfig("launch_gorf/server.conf"); 
		if(server == null) {
			server = new Server().construct();
		}
		Server.getInstance();
	}
	
	public static Server getInstance() {
		return server;
	}

	private Server construct() {
		running = true;
		World.getWorld().setServer(this);
		int cores = 1; // default to one
		
		try {
			Server.print("Determining hardware available...", false);
			cores = Runtime.getRuntime().availableProcessors(); 
		} catch(Exception e) {
			Server.print("ERROR", true);
			Logger.error(e);
		} finally {
			this.taskManager = new TaskManager(cores);
			Server.print(cores + "", true);
		} 

		try { 
			Server.print("Connecting to Login Server", false);
			this.connector = new LoginConnector();
		} catch (Exception e) {
			Server.print("ERROR", true);
			e.printStackTrace();
			Logger.error(e);
		} finally {
			connector.reconnect();
			Server.print("COMPLETE", true);	
		}
		
		try {
			Server.print("Registering Plugins", false);
			PortRegistrar.register();
		} catch (Exception e) {
			Server.print("ERROR", true);
			e.printStackTrace();
		} finally {
			Server.print("COMPLETE", true);
		}

		try {
			Server.print("Configurating Network Bootstraps", false);
			factory = new NioServerSocketChannelFactory(taskManager.getAvailableNettyBosses(), taskManager.getAvailableNettyWorkers());
			ServerBootstrap bootstrap = new ServerBootstrap(factory);
			//ConnectionlessBootstrap udpBootstrap = new ConnectionlessBootstrap();

			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() {
					ChannelPipeline pipeline = Channels.pipeline();
					pipeline.addLast("decoder", new RSCProtocolDecoder());
					pipeline.addLast("encoder", new RSCProtocolEncoder());
					pipeline.addLast("throttler", ConnectionFilter.getInstance(Constants.GameServer.MAX_THRESHOLD));
					pipeline.addLast("handler", new RSCConnectionHandler());
					return pipeline;
				}
			});

			bootstrap.setOption("sendBufferSize", 10000);
			bootstrap.setOption("receiveBufferSize", 10000);
			// the following are experimental 
			bootstrap.setOption("receiveBufferSizePredictorFactory", new AdaptiveReceiveBufferSizePredictorFactory()); // predict buffer from previous packets processed
			bootstrap.setOption("writeBufferLowWaterMark", 32 * 1024); 
			bootstrap.setOption("writeBufferHighWaterMark", 64 * 1024);
			// end 
			bootstrap.setOption("child.tcpNoDelay", true);
			bootstrap.setOption("child.keepAlive", false);

			channel = bootstrap.bind(new InetSocketAddress(Constants.GameServer.SERVER_IP, Constants.GameServer.SERVER_PORT));
		} catch (Exception e) {
			Server.print("ERROR", true);
			e.printStackTrace();
			Logger.error(e);
		}  finally {
			Server.print("COMPLETE", true);
		}
		
		try {
			Server.print("Loading Game Engine", false);
			this.engine = new GameEngine();
		} catch (Exception e) {
			Server.print("ERROR", true);
			Logger.error(e);
			e.printStackTrace();
		} finally {
			Server.print("COMPLETE", true);
			Server.print("\t*** Game Server is ONLINE ***", true);
			engine.start(); 
		}
		return this;
	}

	public boolean isRunning() {
		return running;
	}

	public GameEngine getEngine() {
		return engine;
	}

	public LoginConnector getLoginConnector() {
		return connector;
	}
	
	public TaskManager getTaskManager() {
		return taskManager;
	}

	public boolean isInitialized() {
		return engine != null && connector != null;
	}

	public void kill() {
		Logger.print(Constants.GameServer.SERVER_NAME + " shutting down...");
		running = false;
		engine.emptyWorld();
		connector.kill();
		System.exit(0);
	}

	public boolean shutdownForUpdate(int seconds) {
		if (updateEvent != null) {
			return false;
		}
		updateEvent = new SingleEvent(null, (seconds - 1) * 1000) {
			public void action() {
				kill();
			}
		};
		World.getWorld().getDelayedEventHandler().add(updateEvent);
		return true;
	}

	public int timeTillShutdown() {
		if (updateEvent == null) {
			return -1;
		}
		return updateEvent.timeTillNextRun();
	}

	public void unbind() {
		try {
			channel.close();
		} catch (Exception e) {
		}
	}
}
