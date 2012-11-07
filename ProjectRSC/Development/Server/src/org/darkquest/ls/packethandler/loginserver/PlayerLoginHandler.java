package org.darkquest.ls.packethandler.loginserver;

import java.sql.ResultSet;
import java.util.Map.Entry;

import org.darkquest.config.LoginStatus;
import org.darkquest.ls.Server;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.LSPacket;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packetbuilder.loginserver.PlayerLoginPacketBuilder;
import org.darkquest.ls.packethandler.PacketHandler;
import org.darkquest.ls.util.Config;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;

public final class PlayerLoginHandler implements PacketHandler {

	private final PlayerLoginPacketBuilder builder = new PlayerLoginPacketBuilder();

	public void handlePacket(Packet p, Channel session) throws Exception {
		try {
			final long uID = ((LSPacket) p).getUID();
			World world = (World) session.getAttachment();
			long user = p.readLong();

			String ip = DataConversions.IPToString(p.readLong());
			String pass = p.readString().trim();

			LoginStatus status = validatePlayer(user, pass, ip, world);

			builder.setUID(uID);
			if (status.getCode() == 0 || status.getCode() == 1 || status.getCode() == 99) {

				try {
					Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET `online`=1 WHERE `user`='" + user + "'");
				} catch (Exception e) {
					e.printStackTrace();
				}

				builder.setPlayer(Server.getServer().findSave(user, world), status.getCode());
				world.registerPlayer(user, ip);
			} else {
				builder.setPlayer(null, status.getCode());
			}
			LSPacket packet = builder.getPacket();
			if (packet != null) {
				session.write(packet);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private LoginStatus validatePlayer(long user, String pass, String ip, World world) {
		try {
			ResultSet result = Server.db.getQuery("SELECT `sub_expires`, `pass`, `salt`, `banned`, `owner`, `group_id` FROM `" + Config.MYSQL_TABLE_PREFIX + "players` WHERE `user`='" + user + "'");

			if (!result.next()) {
				return LoginStatus.INVALID_CREDENTIALS;
			}

			String hashedPassword = DataConversions.hmac("SHA512", result.getString("salt") + pass, Config.HASH_PRIVATE_KEY);

			if (!hashedPassword.equals(result.getString("pass"))) {
				return LoginStatus.INVALID_CREDENTIALS;
			}

			long banned = result.getLong("banned");

			if (banned == 1) {
				return LoginStatus.PERM_DISABLED;
			} else if (banned != 0) {
				return LoginStatus.TEMP_DISABLED;
			}

			if (world.isMembers()) {
				long sub_expires = result.getLong("sub_expires");
				long difference = sub_expires - (System.currentTimeMillis() / 1000);

				if (difference <= 0) {
					return LoginStatus.MEMBER_ACCOUNT_NEEDED;
				}
			}

			if (result.getInt("group_id") == 1 || result.getInt("group_id") == 2) {
				return LoginStatus.ACCEPTED_ADMIN;
			}

			int owner = result.getInt("owner");
			for (World w : Server.getServer().getWorlds()) {
				for (Entry<Long, Integer> player : w.getPlayers()) {
					if (player.getKey() == user) {
						return LoginStatus.ALREADY_LOGGED_IN;
					}
					if (player.getValue() == owner) {
						return LoginStatus.OWNER_LOGGED_IN;
					}
				}
				if (w.hasPlayer(user)) {
					return LoginStatus.ALREADY_LOGGED_IN;
				}
			}
			return LoginStatus.ACCEPTED;
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("uh oh :(");
		return LoginStatus.FAILED_TO_DECODE;
	}
}
