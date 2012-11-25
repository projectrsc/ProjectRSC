package org.darkquest.ls.codec;

import org.darkquest.ls.Server;

import org.darkquest.ls.net.FPacket;
import org.jboss.netty.buffer.ByteBufferBackedChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.URLEncoder;
import java.nio.ByteBuffer;


/**
 * Encodes the high level <code>FPacket</code> class into the proper protocol
 * data required for transmission.
 */
public class FProtocolEncoder extends StringEncoder {
    /**
     * Converts a <code>RSCPacket</code> object into the raw data needed for
     * transmission.
     *
     * @param ctx     The context
     * @param channel The channel
     * @param msg     The message
     */
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object message) {
        if (!(message instanceof FPacket)) {
            Server.error(new Exception("Wrong packet type! " + message.toString()));
            return ChannelBuffers.EMPTY_BUFFER;
        }
        FPacket p = (FPacket) message;
        try {
            String s = String.valueOf(p.getID());
            if (p.countParameters() > 0) {
                for (String param : p.getParameters()) {
                    s += " " + URLEncoder.encode(param, "UTF-8");
                }
            }
            byte[] data = s.getBytes();

            ByteBuffer buffer = ByteBuffer.allocate(data.length);
            buffer.put(data, 0, data.length);
            buffer.flip();
            return new ByteBufferBackedChannelBuffer(buffer);
        } catch (Exception e) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
    }
}
