package org.darkquest.ls.codec;

import org.darkquest.gs.connection.RSCPacket;
import org.darkquest.gs.util.Logger;
import org.darkquest.ls.net.LSPacket;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;


/**
 * A decoder for the LS protocol. Parses the incoming data from an IoSession and
 * outputs it as a <code>LSPacket</code> object.
 */
public class LSProtocolDecoder extends FrameDecoder {
	/**
	 * Parses the data in the provided byte buffer and writes it to
	 * <code>out</code> as a <code>RSCPacket</code>.
	 *
	 * @param ctx     The context of the channel handler
	 * @param channel The channel
	 * @param msg     The message
	 * @return Whether enough data was available to create a packet
	 */
	/*
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer msg) {


            if (msg.readableBytes() >= 13) {
                int length = msg.readInt();
                if (length <= msg.readableBytes()) {
                    byte[] payload = new byte[length - 9];

                    int id = msg.readUnsignedByte();
                    long uid = msg.readLong();
                    msg.readBytes(payload);

                    return new LSPacket(channel, id, uid, payload);
                }
            }
            return null;


	if (in.remaining() >= 13) {
	    int length = in.getInt();
	    if (length <= in.remaining()) {
		byte[] payload = new byte[length - 9];

		int id = in.getUnsigned();
		long uid = in.getLong();
		in.get(payload);

		out.write(new LSPacket(session, id, uid, payload));
		return true;
	    } else {
		in.rewind();
		return false;
	    }
	}


    }*/

	@Override
	protected Object decode(ChannelHandlerContext arg0, Channel channel,
			ChannelBuffer message) throws Exception {

		if (message instanceof ChannelBuffer) {
			ChannelBuffer msg = (ChannelBuffer) message;
			if (msg.readableBytes() >= 13) {
				msg.markReaderIndex();
				int length = msg.readInt();
				//if (length - 1 >= 0) {
					if (length <= msg.readableBytes()) {
						byte[] payload = new byte[length - 9];

						int id = msg.readUnsignedByte();
						long uid = msg.readLong();
						msg.readBytes(payload);

						return new LSPacket(channel, id, uid, payload);
					} else {
						msg.resetReaderIndex();
						return null;
						//Logger.println("Negative array length! id=" + msg.readUnsignedByte() + ",len=" + length);
						
					}
				//}
			} else {
				return null;
			}
			
		}
		return null;

	}
}
