package com.prsc.gs.core;

import java.net.InetSocketAddress;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.prsc.config.Constants;
import com.prsc.gs.Server;
import com.prsc.gs.builders.ls.MiscPacketBuilder;
import com.prsc.gs.connection.LSConnectionHandler;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.LSProtocolDecoder;
import com.prsc.gs.connection.LSProtocolEncoder;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.registrar.PortRegistrar;
import com.prsc.gs.registrar.impl.PacketHandlers;
import com.prsc.gs.util.Logger;

public final class LoginConnector {

	private final MiscPacketBuilder actionSender = new MiscPacketBuilder(this);

	private final SimpleChannelHandler connectionHandler = new LSConnectionHandler(this);
	
	private final Map<Long, PacketHandler> uniqueHandlers = new TreeMap<Long, PacketHandler>();

	private final Queue<LSPacket> packetQueue = new ConcurrentLinkedQueue<LSPacket>();

	private boolean registered = false;

	private boolean running = true;

	private int connectionAttempts = 0;

	private Channel session;

	private ChannelFactory factory;

	public LoginConnector() {
		this.factory = new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newCachedThreadPool());
	}

	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	public Queue<LSPacket> getPacketQueue() {
		return packetQueue;
	}
	
	public Map<Long, PacketHandler> getUniqueHandlers() {
		return uniqueHandlers;
	}
	
	public void pushToMessageQueue(LSPacket packet) {
		packetQueue.add(packet);
	}

	public Channel getSession() {
		return session;
	}

	public boolean isRegistered() {
		return registered;
	}

	public SimpleChannelHandler getConnectionHandler() {
		return connectionHandler;
	}

	public boolean isRunning() {
		return running;
	}

	public void kill() {
		running = false;
		Logger.print("Unregistering world with Login Server");
		actionSender.unregisterWorld();
	}
	
	public void processIncomingPackets() {
		for(LSPacket p : packetQueue) {
			PacketHandler handler = null;
			if (((handler = Server.getInstance().getLoginConnector().getUniqueHandlers().get(p.getUID())) != null) 
					|| ((handler = PortRegistrar.lookup(PacketHandlers.class).getLoginHandlers().get(p.getID())) != null)) {
				try {
					handler.handlePacket(p, Server.getInstance().getLoginConnector().getSession());
					Server.getInstance().getLoginConnector().getUniqueHandlers().remove(p.getUID());
				} catch (Exception e) {
					Logger.error("Exception with p[" + p.getID() + "] from LOGIN_SERVER: " + e.getMessage());
				}
			} else {
				Logger.error("Unhandled packet from LS: " + p.getID());
			}
		}
	}

	public boolean reconnect() {
		try {
			ClientBootstrap bootstrap = new ClientBootstrap(factory);
			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() {
					ChannelPipeline pipeline = Channels.pipeline();
					pipeline.addLast("decoder", new LSProtocolDecoder());
					pipeline.addLast("encoder", new LSProtocolEncoder());
					pipeline.addLast("handler", connectionHandler);
					return pipeline;
				}
			});

			bootstrap.setOption("tcpNoDelay", true);
			bootstrap.setOption("keepAlive", true);

			ChannelFuture future = bootstrap.connect(new InetSocketAddress(Constants.GameServer.LOGIN_SERVER_IP, Constants.GameServer.LOGIN_SERVER_PORT));
			future.await();

			if (future.isDone() && (future.isSuccess() || future.getCause() == null)) {
				session = future.getChannel();
				actionSender.registerWorld();
				connectionAttempts = 0;
				return true;
			} else if (future.isDone()) {
				future.getCause().printStackTrace();
				Logger.println("raro " + future.getCause());
			}
			if (connectionAttempts++ >= 100) {
				Logger.println("Connection to the LoginServer has been attempted but cannot be established!");
				System.exit(1);
				return false;
			}
			Thread.sleep(1000);
			return reconnect();
		} catch (Exception e) {
			Logger.println("Error connecting to Login Server: " + e.getMessage());
			return false;
		}
	}

	public void setHandler(long uID, PacketHandler handler) {
		uniqueHandlers.put(uID, handler);
	}

	public void setRegistered(boolean registered) {
		if (registered) {
			this.registered = true;
		} else {
			Logger.error(new Exception("Error registering world"));
		}
	}

}
