package org.darkquest.gs.connection;

import org.darkquest.gs.model.Player;
import org.darkquest.gs.util.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * A decoder for the RSC protocol. Parses the incoming data from a Channel
 * and outputs it as a <code>RSCPacket</code> object.
 */
public final class RSCProtocolDecoder extends FrameDecoder {
	/**
	 * Parses the data in the provided byte buffer and writes it to
	 * <code>out</code> as a <code>RSCPacket</code>.
	 *
	 * @param ctx     The context of the channel handler
	 * @param channel The channel
	 * @param msg     The message
	 * @return Whether enough data was available to create a packet
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer msg) {
		try {
		if (msg.readableBytes() >= 3) {
			msg.markReaderIndex();
			byte[] buf = new byte[]{ msg.readByte(), msg.readByte() };
			int length = ((short) ((buf[0] & 0xff) << 8) | (short) (buf[1] & 0xff));

			if (length <= msg.readableBytes()) {
					
					byte[] payload = new byte[length - 1];
					int id = msg.readByte() & 0xff;
					msg.readBytes(payload);
					RSCPacket p = new RSCPacket(channel, id, payload);
					return p;
				
			} else {
				msg.resetReaderIndex();
				return null;
			}
		} else {
			return null;
		}
		
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}




