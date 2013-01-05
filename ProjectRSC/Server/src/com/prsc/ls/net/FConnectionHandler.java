package com.prsc.ls.net;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.*;
import com.prsc.ls.Server;
import com.prsc.ls.util.Config;


/**
 * Handles the protocol events fired from MINA.
 */
public class FConnectionHandler extends SimpleChannelHandler {

    /**
     * Invoked whenever a packet is ready to be added to the queue.
     *
     * @param ctx The channel chandler context
     * @param e   The message event
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel ch = ctx.getChannel();
        if(e.getMessage() instanceof FPacket) {
        	if (ch.isConnected()) {
        		FPacket packet = (FPacket) e.getMessage();
        		Server.getServer().getEngine().pushToMessageStack(packet);
        	}
        }
    }
    
    @Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	String ip = ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress();
		
		if(!ip.equalsIgnoreCase("127.0.0.1") && Config.LS_IP.equalsIgnoreCase("localhost")) {
			ctx.getChannel().disconnect();
			return;
		} 
		System.out.println("Connection from (frontend): " + ip);
		Channel session = ctx.getChannel();
		session.setAttachment(session);
	}
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {

         
    }

    /**
     * Invoked whenever a packet is sent.
     *
     * @param ctx The channel chandler context
     * @param e   The message event
     */
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) {
        ctx.getChannel().disconnect();
    }

    /**
     * Invoked whenever a channel is connected
     *
     * @param ctx The channel chandler context
     * @param e   The state event
     */
    public void channelOpened(ChannelHandlerContext ctx, ChannelStateEvent e) {
        //TODO: Add filter the specific protocol
        //session.getFilterChain().addFirst("protocolFilter", new ProtocolCodecFilter(new FCodecFactory()));
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
