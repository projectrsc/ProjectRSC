package org.darkquest.gs.connection;

import org.darkquest.gs.Server;
import org.darkquest.gs.core.LoginConnector;
import org.darkquest.gs.util.Logger;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.*;


/**
 * Handles the protocol events fired from MINA.
 */
public class LSConnectionHandler extends SimpleChannelHandler {
	/**
	 * A reference to the login connector
	 */
	private final LoginConnector connector;

	/**
	 * Creates a new connection handler for the given login connector.
	 *
	 * @param connector The connector in use
	 */
	public LSConnectionHandler(LoginConnector connector) {
		this.connector = connector;
	}

	/**
	 * Invoked whenever a packet is ready to be added to the queue.
	 *
	 * @param ctx The channel chandler context
	 * @param e   The message event
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		Channel ch = ctx.getChannel();
		if(e.getMessage() instanceof LSPacket) {
			if (ch.isConnected()) {
				connector.getPacketQueue().add((LSPacket) e.getMessage());
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		e.getCause().printStackTrace();
		System.out.println("LS EXCEPTION: " + e.getCause().getMessage());

	}

	/**
	 * Invoked whenever a a channel is closed. This must handle unregistering
	 * the disconnecting world from the engine.
	 *
	 * @param ctx The channel chandler context
	 * @param e   The state event
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Server server = World.getWorld().getServer();
		if (server != null && server.isRunning()) {
			Logger.error(new Exception("Lost connection to the login server!"));
		}
	}

	/**
	 * Invoked whenever a channel is connected
	 *
	 * @param ctx The channel chandler context
	 * @param e   The state event
	 */
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
		//TODO: Add filter the specific protocol
		//session.getFilterChain().addFirst("protocolFilter", new ProtocolCodecFilter(new LSCodecFactory()));
	}
}
