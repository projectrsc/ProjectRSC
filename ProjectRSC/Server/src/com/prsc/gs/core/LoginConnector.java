package com.prsc.gs.core;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.TreeMap;
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
import com.prsc.gs.builders.ls.MiscPacketBuilder;
import com.prsc.gs.connection.LSConnectionHandler;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.LSProtocolDecoder;
import com.prsc.gs.connection.LSProtocolEncoder;
import com.prsc.gs.connection.PacketQueue;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.phandler.PacketHandlerDef;
import com.prsc.gs.util.Logger;
import com.prsc.gs.util.PersistenceManager;

public final class LoginConnector {

	private final MiscPacketBuilder actionSender = new MiscPacketBuilder(this);

	private final SimpleChannelHandler connectionHandler = new LSConnectionHandler(this);

	private final TreeMap<Integer, PacketHandler> packetHandlers = new TreeMap<Integer, PacketHandler>();

	private final TreeMap<Long, PacketHandler> uniqueHandlers = new TreeMap<Long, PacketHandler>();

	private PacketQueue<LSPacket> packetQueue;

	private boolean registered = false;

	private boolean running = true;

	private int connectionAttempts = 0;

	private Channel session;

	private ChannelFactory factory;

	public LoginConnector() {
		packetQueue = new PacketQueue<LSPacket>();
		factory = new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newCachedThreadPool());
		loadPacketHandlers();
	}

	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	public PacketQueue<LSPacket> getPacketQueue() {
		return packetQueue;
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

	public TreeMap<Integer, PacketHandler> getPacketHandlers() {
		return packetHandlers;
	}

	public boolean isRunning() {
		return running;
	}

	public TreeMap<Long, PacketHandler> getUniqueHandlers() {
		return uniqueHandlers;
	}

	public void kill() {
		running = false;
		Logger.print("Unregistering world with Login Server");
		actionSender.unregisterWorld();
	}

	private void loadPacketHandlers() {
		PacketHandlerDef[] handlerDefs = (PacketHandlerDef[]) PersistenceManager.load("LSPacketHandlers.xml");
		for (PacketHandlerDef handlerDef : handlerDefs) {
			try {
				String className = handlerDef.getClassName();
				Class<?> c = Class.forName(className);
				if (c != null) {
					PacketHandler handler = (PacketHandler) c.newInstance();
					for (int packetID : handlerDef.getAssociatedPackets()) {
						packetHandlers.put(packetID, handler);
					}
				}
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	public void processIncomingPackets() {
		for (LSPacket p : packetQueue.getPackets()) {
			PacketHandler handler;
			if (((handler = uniqueHandlers.get(p.getUID())) != null) || ((handler = packetHandlers.get(p.getID())) != null)) {
				try {
					handler.handlePacket(p, session);
					uniqueHandlers.remove(p.getUID());
				} catch (Exception e) {
					//Logger.error("Exception with p[" + p.getID() + "] from LOGIN_SERVER: " + e.getMessage());
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
			}//Fucked
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

	public void sendQueuedPackets() {
		try {
			List<LSPacket> packets = actionSender.getPackets();
			for (LSPacket packet : packets) {
			
				session.write(packet);
			}
		} catch (Exception e) {
			Logger.println("Stack processInc: ");
			e.printStackTrace();
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
