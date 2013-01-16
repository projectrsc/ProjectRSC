package com.prsc.ls;

import java.io.File;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import com.prsc.ls.codec.FProtocolDecoder;
import com.prsc.ls.codec.FProtocolEncoder;
import com.prsc.ls.codec.LSProtocolDecoder;
import com.prsc.ls.codec.LSProtocolEncoder;
import com.prsc.ls.core.LoginEngine;
import com.prsc.ls.core.TaskManager;
import com.prsc.ls.model.PlayerSave;
import com.prsc.ls.model.World;
import com.prsc.ls.net.DatabaseConnection;
import com.prsc.ls.net.FConnectionHandler;
import com.prsc.ls.net.LSConnectionHandler;
import com.prsc.ls.util.Config;

public final class Server {

	public static DatabaseConnection db;

	private static Server server;

	public static void print(String str, boolean newline) {
		System.out.printf("%-32s" + (newline ? "\n" : ""), str);
	}

	public static void error(Object o) {
		if (o instanceof Exception) {
			Exception e = (Exception) o;
			e.printStackTrace();
			System.exit(1);
			return;
		}
		System.err.println(o.toString());
	}

	public static Server getServer() {
		if (server == null) {
			server = new Server();
		}
		return server;
	}

	public static void main(String[] args) throws IOException {
		String configFile = "ls.conf";
		//String configFile = "launch_gorf/ls.conf";
		if (args.length > 0) {
			File f = new File(args[0]);
			if (f.exists()) {
				configFile = f.getName();	
			}
		}
		System.out.printf("\t*** ProjectRSC Login Server ***\n\n");
		
		// TODO: Organize this rofl
		try {
			print("Loading Config...", false);
			Config.initConfig(configFile);
		} catch(Exception e) {
			print("ERROR", true);
		} finally {
			print("COMPLETE", true);
		}
		
		try {
			print("Connecting SQL...", false);
			db = new DatabaseConnection();
		} catch(Exception e) {
			print("ERROR", true);
		} finally {
			print("COMPLETE", true);
		}
		
		try {
			print("Clearing Online Characters", false);
			db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET online=0");
		} catch (SQLException e) {
			print("ERROR", true);
			error(e);
		} finally {
			print("COMPLETE", true);
		}
		
		//print("Creating Monitor...", false); WILL REIMPLEMENT WHEN PROXY DETECTION IS ADDED IN
		//Monitor.getInstance();
		//print("COMPLETE", true);

		Server.getServer();

	}

	private LoginEngine engine;
	
	private TaskManager manager;

	private Channel frontendAcceptor;

	private Channel serverAcceptor;

	private final TreeMap<Integer, World> idleWorlds = new TreeMap<Integer, World>();

	private final TreeMap<Integer, World> worlds = new TreeMap<Integer, World>();

	private Server() {
		try {
			print("Starting TaskManager", false);
			manager = new TaskManager();
		} catch(Exception e) {
			print("ERROR", true);
			e.printStackTrace();
		} finally {
			print("COMPLETE", true);
		}
		
		try {
			print("Initializing LoginEngine", false);
			engine = new LoginEngine();
			engine.start();
		} catch(Exception e) {
			print("ERROR", true);
			e.printStackTrace();
		} finally {
			print("COMPLETE", true);
		}

		try {
			print("Initializing Server Listener", false);
			serverAcceptor = createListener(Config.LS_IP, Config.LS_PORT, new LSConnectionHandler(), new LSProtocolEncoder(), new LSProtocolDecoder());
		} catch(Exception e) {
			print("ERROR", true);
			e.printStackTrace();
		} finally {
			print("COMPLETE", true);
		}
		
		try {
			print("Initializing Frontend Listener", false);
			frontendAcceptor = createListener(Config.QUERY_IP, Config.QUERY_PORT, new FConnectionHandler(), new FProtocolEncoder(), new FProtocolDecoder());
		} catch(Exception e) {
			print("ERROR", true);
			e.printStackTrace();
		} finally {
			print("COMPLETE", true);
		} 

		print("\t*** Login Server is ONLINE ***", true);
	}

	private Channel createListener(String ip, int port, final SimpleChannelHandler handler, final OneToOneEncoder encoder, final FrameDecoder decoder) throws IOException {
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", decoder);
				pipeline.addLast("encoder", encoder);
				pipeline.addLast("handler", handler);
				return pipeline;
			}
		});

		bootstrap.setOption("sendBufferSize", 10000);
		bootstrap.setOption("receiveBufferSize", 10000);
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", false);

		return bootstrap.bind(new InetSocketAddress(ip, port));
	}
	
	private Channel createListener(String ip, int port, final SimpleChannelHandler handler, final StringEncoder encoder, final StringDecoder decoder) throws IOException {
		// We'll only need one boss and worker thread since it should only be access by localhost
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor()));

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
				        8192, Delimiters.lineDelimiter()));
				pipeline.addLast("decoder", decoder);
				pipeline.addLast("encoder", encoder);
				pipeline.addLast("handler", handler);
				return pipeline;
			}
		});

		bootstrap.setOption("sendBufferSize", 10000);
		bootstrap.setOption("receiveBufferSize", 10000);
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", false);

		return bootstrap.bind(new InetSocketAddress(ip, port));
	}

	public PlayerSave findSave(long user, World world) {
		/*
		if(!world.isBlocking() && world.getPlayerCache().contains(user)) {
			System.out.println("Found cached save for " + DataConversions.hashToUsername(user));
			return world.getPlayerCache().get(user);
		} */
		return PlayerSave.loadPlayer(user);
	}

	public World findWorld(long user) {
		for (World w : getWorlds()) {
			if (w.hasPlayer(user)) {
				return w;
			}
		}
		return null;
	}

	public LoginEngine getEngine() {
		return engine;
	}
	
	public TaskManager getTaskManager() {
		return manager;
	}

	public World getIdleWorld(int id) {
		return idleWorlds.get(id);
	}

	public World getWorld(int id) {
		if (id < 0) {
			return null;
		}
		return worlds.get(id);
	}

	public Collection<World> getWorlds() {
		return worlds.values();
	}

	public boolean isRegistered(World world) {
		return getWorld(world.getID()) != null;
	}

	public void kill() {
		try {
			serverAcceptor.close();
			frontendAcceptor.close();
			db.close();
		} catch (Exception e) {
			Server.error(e);
		}
	}

	public boolean registerWorld(World world) {
		int id = world.getID();
		if (id < 0 || getWorld(id) != null) {
			return false;
		}
		worlds.put(id, world);
		return true;
	}

	public void setIdle(World world, boolean idle) {
		if (idle) {
			worlds.remove(world.getID());
			idleWorlds.put(world.getID(), world);
		} else {
			idleWorlds.remove(world.getID());
			worlds.put(world.getID(), world);
		}
	}

	public boolean unregisterWorld(World world) {
		int id = world.getID();
		if (id < 0) {
			return false;
		}
		if (getWorld(id) != null) {
			worlds.remove(id);
			return true;
		}
		if (getIdleWorld(id) != null) {
			idleWorlds.remove(id);
			return true;
		}
		return false;
	}
}
