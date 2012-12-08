package com.prsc.gs;

import java.io.IOException;



import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

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
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.event.SingleEvent;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.service.Services;
import com.prsc.gs.util.Logger;
import com.prsc.gs.world.World;

public final class Server {
	

	public static void print(String str, boolean newline) {
		System.out.printf("%-32s" + (newline ? "\n" : ""), str);
	}

	public static void main(String[] args) throws IOException {//Registering
		System.out.printf("\t*** ProjectRSC Game Server ***\n\n");
		//Constants.GameServer.initConfig("server.conf");
		Constants.GameServer.initConfig("launch_gorf/server.conf"); 
		new Server();
	}

	private Channel channel;

	private LoginConnector connector;

	private GameEngine engine;

	private boolean running;

	private DelayedEvent updateEvent;

	private NioServerSocketChannelFactory factory;
	
	//private NioDatagramChannelFactory udpFactory;

	public Server() {
		running = true;
		World.getWorld().setServer(this);
		
		try {
			Server.print("Loading Plugins", false);
			PluginHandler.getPluginHandler().initPlugins();
		} catch (Exception e) {
			Server.print("ERROR", true);
			Logger.error(e);
		} finally {
			Server.print("COMPLETE", true);
		}

		try {
			Server.print("Loading Login Connector", false);
			connector = new LoginConnector();
		} catch (Exception e) {
			Server.print("ERROR", true);
			e.printStackTrace();
		} finally {
			Server.print("COMPLETE", true);
		}

		/**
		 * i had to move this up because the friend packet handler threw an exception due to a constant value
		 * 		-hikilaka
		 */
		try { 
			Server.print("Connecting to Login Server", false);
			connector.reconnect();
		} catch (Exception e) {
			Server.print("ERROR", true);
			e.printStackTrace();
			Logger.error(e);
		} finally {
			Server.print("COMPLETE", true);	
		}
		
		try {
			Server.print("Initializing Services", false);
			Services.init();
		} catch (Exception e) {
			Server.print("ERROR", true);
			e.printStackTrace();
		} finally {
			Server.print("COMPLETE", true);
		}

		try {
			Server.print("Loading Game Engine", false);
			engine = new GameEngine();
			engine.start();
		} catch (Exception e) {
			Server.print("ERROR", true);
			Logger.error(e);
			e.printStackTrace();
		} finally {
			Server.print("COMPLETE", true);
		}
		
		try {
			Server.print("Starting Services", false);
			Services.start();
		} catch (Exception e) {
			Server.print("ERROR", true);
			e.printStackTrace();
		} finally {
			Server.print("COMPLETE", true);
		}
		//int cores = 1; // default to one

		try {
			Server.print("Initializing NIO", false);
			//cores = Runtime.getRuntime().availableProcessors() * 2; // TODO: split between udp/tcp
			// According to Netty docs, we'll need at least cores * 2 for workers, and one thread per boss (listening port)
			factory = new NioServerSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newCachedThreadPool());
		} catch (Exception e) {
			Server.print("ERROR", true);
			e.printStackTrace();
			Logger.error(e);
		} finally {
			Server.print("COMPLETE", true);
		}

		try {
			Server.print("Configurating Bootstraps", false);
			ServerBootstrap bootstrap = new ServerBootstrap(factory);
			//ConnectionlessBootstrap udpBootstrap = new ConnectionlessBootstrap();

			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() {
					ChannelPipeline pipeline = Channels.pipeline();
					pipeline.addLast("decoder", new RSCProtocolDecoder());
					pipeline.addLast("encoder", new RSCProtocolEncoder());
					pipeline.addLast("throttler", ConnectionFilter.getInstance(Constants.GameServer.MAX_THRESHOLD));
					pipeline.addLast("handler", new RSCConnectionHandler(engine));
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

		Server.print("\t*** Game Server is ONLINE ***", true);
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
