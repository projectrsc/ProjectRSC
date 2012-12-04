package com.prsc.ls.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.string.StringDecoder;

import com.prsc.ls.net.FPacket;

import java.net.URLDecoder;
import java.nio.charset.Charset;


/**
 * A decoder for the Frontend protocol. Parses the incoming data from an
 * IoSession and outputs it as a <code>FPacket</code> object.
 */
public class FProtocolDecoder extends StringDecoder {
	
	public FProtocolDecoder() {
		super(Charset.forName("UTF-8"));
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
	protected Object decode(ChannelHandlerContext ctx, Channel channel, Object message) {
		if (message instanceof ChannelBuffer) {
			ChannelBuffer msg = (ChannelBuffer) message;
			String s = msg.toString(Charset.forName("UTF-8")).trim();
			//System.out.println("Message: " + s);
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
