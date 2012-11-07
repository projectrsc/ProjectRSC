package org.darkquest.gs.connection;

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
	@Override
	protected Object decode(ChannelHandlerContext arg0, Channel arg1, ChannelBuffer msg) throws Exception {


		if (msg.readableBytes() >= 13) {
			msg.markReaderIndex();
			int length = msg.readInt();
			if (length <= msg.readableBytes()) {
				byte[] payload = new byte[length - 9];
				int id = msg.readUnsignedByte();
				long uid = msg.readLong();
				msg.readBytes(payload);

				return new LSPacket(null, id, uid, payload);
			}  else {
				msg.resetReaderIndex();
				return null;

			}
		} else {
			return null;
		}
	}


}
