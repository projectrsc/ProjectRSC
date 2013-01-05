package com.prsc.ls.net;

import java.net.InetSocketAddress;


import org.jboss.netty.channel.*;

import com.prsc.ls.Server;
import com.prsc.ls.model.World;
import com.prsc.ls.util.Config;


/**
 * Handles the protocol events fired from .
 */
public class LSConnectionHandler extends SimpleChannelHandler {
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		String ip = ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress();
		
		if(!ip.equalsIgnoreCase("127.0.0.1") && Config.LS_IP.equalsIgnoreCase("localhost")) {
			ctx.getChannel().disconnect();
			return;
		} 
		
		Channel session = ctx.getChannel();
		session.setAttachment(session);
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
				LSPacket packet = (LSPacket) e.getMessage();
				Server.getServer().getEngine().pushToMessageStack(packet);
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
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		World world = (World) ctx.getAttachment();
		if (world != null) {
			Server.getServer().setIdle(world, true);
			world.clearPlayers();
			Server.error("Connection to world " + world.getID() + " lost!");
		}
	}

	/**
	 * Invoked whenever a channel is unbinded
	 *
	 * @param ctx The channel chandler context
	 * @param e   The state event
	 */
	public void unbindRequested(ChannelHandlerContext ctx, ChannelStateEvent e) {
		ctx.getChannel().disconnect();
	}
}
