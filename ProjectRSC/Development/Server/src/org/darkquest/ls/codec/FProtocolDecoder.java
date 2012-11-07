package org.darkquest.ls.codec;

import org.darkquest.ls.Server;
import org.darkquest.ls.net.FPacket;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


/**
 * A decoder for the Frontend protocol. Parses the incoming data from an
 * IoSession and outputs it as a <code>FPacket</code> object.
 */
public class FProtocolDecoder extends FrameDecoder {
	private static CharsetDecoder stringDecoder;

	static {
		try {
			stringDecoder = Charset.forName("UTF-8").newDecoder();
		} catch (Exception e) {
			Server.error(e);
		}
	}

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
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer message) {

		if (message instanceof ChannelBuffer) {
			ChannelBuffer msg = (ChannelBuffer) message;
			String s = msg.toString().trim();
			int delim = s.indexOf(" ");

			int id;
			String[] params;
			if (delim > -1) {
				id = Integer.parseInt(s.substring(0, delim));
				params = s.substring(delim + 1).split(" ");
			} else {
				id = Integer.parseInt(s);
				params = new String[0];
			}
			try {
				for (int i = 0; i < params.length; i++) {
					params[i] = URLDecoder.decode(params[i], "UTF-8");
				}
			} catch (Exception e) {
				return null;
			}
			return new FPacket(channel, id, params);
		}
		return null;
	}





}
