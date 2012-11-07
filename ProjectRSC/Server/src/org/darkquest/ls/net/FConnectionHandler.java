package org.darkquest.ls.net;

import org.darkquest.ls.LoginEngine;
import org.jboss.netty.channel.*;
//import org.darkquest.ls.codec.FCodecFactory;


/**
 * Handles the protocol events fired from MINA.
 */
public class FConnectionHandler extends SimpleChannelHandler {
    /**
     * A reference to the login engine
     */
    private LoginEngine engine;

    /**
     * Creates a new connection handler for the given login engine.
     *
     * @param engine The engine in use
     */
    public FConnectionHandler(LoginEngine engine) {
        this.engine = engine;
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
        if (ch.isConnected()) {
            engine.getFPacketQueue().add((FPacket) e.getMessage());
        }
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
