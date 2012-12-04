package com.prsc.ls.codec;

import org.jboss.netty.buffer.ByteBufferBackedChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.prsc.gs.util.Logger;
import com.prsc.ls.Server;
import com.prsc.ls.net.LSPacket;

import java.nio.ByteBuffer;


/**
 * Encodes the high level <code>LSPacket</code> class into the proper protocol
 * data required for transmission.
 */
public class LSProtocolEncoder extends OneToOneEncoder {
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
        if (!(message instanceof LSPacket)) {
            Server.error(new Exception("Wrong packet type! " + message.toString()));
            return ChannelBuffers.EMPTY_BUFFER;
        }
        LSPacket p = (LSPacket) message;
        byte[] data = p.getData();
        int packetLength = data.length;
        ByteBuffer buffer;
        if (!p.isBare()) {
            buffer = ByteBuffer.allocate(data.length + 13);
            packetLength += 9;

            buffer.putInt(packetLength);
            int id = p.getID();
            buffer.put((byte) id);
            buffer.putLong(p.getUID());
        } else {
            buffer = ByteBuffer.allocate(data.length);
        }
        buffer.put(data, 0, data.length);
        buffer.flip();
        return new ByteBufferBackedChannelBuffer(buffer);
        
        
        
        /*if (!(message instanceof LSPacket)) {
			Logger.error(new Exception("Wrong packet type! " + message.toString()));
			return ChannelBuffers.EMPTY_BUFFER;
		}
		LSPacket p = (LSPacket) message;
		byte[] data = p.getData();
		int dataLength = data.length;
		ByteBuffer buffer;
		if (!p.isBare()) {
			buffer = ByteBuffer.allocate(dataLength + 3);
			byte[] outlen = {(byte) (dataLength >> 8), (byte) (dataLength)};
			buffer.put(outlen);
			int id = p.getID();
			buffer.put((byte) id);
		} else {
			buffer = ByteBuffer.allocate(dataLength);
		}
		buffer.put(data, 0, dataLength);
		buffer.flip();
		return new ByteBufferBackedChannelBuffer(buffer);*/
    }
}
