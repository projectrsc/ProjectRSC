package org.darkquest.gs.phandler.client;

import java.net.InetSocketAddress;

import org.darkquest.config.Constants;
import org.darkquest.config.LoginStatus;
import org.darkquest.gs.builders.RSCPacketBuilder;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.service.Services;
import org.darkquest.gs.service.impl.InvalidLoginHandler;
import org.darkquest.gs.util.RSA;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

public class PlayerLogin implements PacketHandler {

	private final InvalidLoginHandler loginHandler = Services.lookup(InvalidLoginHandler.class);
	
	public void handlePacket(Packet p1, Channel session) throws Exception {
		Player player = (Player) session.getAttachment();
		final String ip = ((InetSocketAddress) session.getRemoteAddress()).getAddress().toString().replaceAll("/", "");

		LoginStatus status = LoginStatus.FAILED_TO_DECODE;

		try {
			byte[] data = RSA.decrypt(p1.getData());
			Packet p = new Packet(session, data);

			boolean reconnecting = (p.readByte() == 1);
			int clientVersion = p.readInt();

			int[] sessionKeys = new int[4];
			for (int key = 0; key < sessionKeys.length; key++) {
				sessionKeys[key] = p.readInt();
			}
			
			String username = p.readString(20).trim();
			String password = p.readString(20).trim();

			if (!loginHandler.acceptConnection(ip)) {
				status = LoginStatus.LOGIN_ATTEMPTS_EXCEEDED;
				loginHandler.incrementConnectionAttempt(ip); // reset the current timer
			} else if (World.getWorld().countPlayers() >= Constants.GameServer.MAX_PLAYERS) {
				status = LoginStatus.WORLD_FULL;
			} else if (clientVersion < Constants.GameServer.CLIENT_VERSION) {
				status = LoginStatus.CLIENT_UPDATED;
			} else if (!player.setSessionKeys(sessionKeys)) {
				status = LoginStatus.REJECTED_SESSION;
			} else {
				player.load(username, password, 0, reconnecting);
				return;
			}
		} catch (Exception e) {
			System.err.println("Login exception with: " + ip);
			e.printStackTrace();
			status = LoginStatus.FAILED_TO_DECODE;
		}

		RSCPacketBuilder pb = new RSCPacketBuilder();
		pb.setBare(true);
		pb.addByte(status.getCode());
		session.write(pb.toPacket());
		player.destroy(true);
	}
}
