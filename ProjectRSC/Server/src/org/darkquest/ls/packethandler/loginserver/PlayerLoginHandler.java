package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.Server;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.LSPacket;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.net.filter.ConnectionFilter;
import org.darkquest.ls.packetbuilder.loginserver.PlayerLoginPacketBuilder;
import org.darkquest.ls.packethandler.PacketHandler;
import org.darkquest.ls.util.Config;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

/**
 * TODO: Rewrite
 * @author openfrog
 *
 */

public final class PlayerLoginHandler implements PacketHandler {

	private final PlayerLoginPacketBuilder builder = new PlayerLoginPacketBuilder();

	public void handlePacket(Packet p, Channel session) throws Exception {
		try {
			final long uID = ((LSPacket) p).getUID();
			World world = (World) session.getAttachment();
			long user = p.readLong();
			
			long encoded = p.readLong();
			String ip = DataConversions.IPToString(encoded);
			String pass = p.readString().trim();
			byte loginCode = validatePlayer(user, pass, ip, world); 
			
			if(ConnectionFilter.getInstance(0).isMaxed(DataConversions.IPToLong(ip))) {
				loginCode = 8;
			}
			
			builder.setUID(uID);
			if (loginCode == 0 || loginCode == 1 || loginCode == 99) {//
				// START
				/*
				if(ConnectionFilter.getInstance(0) != null) {
					int initialCount = ConnectionFilter.getInstance(0).incrementAndGet(DataConversions.IPToLong(ip));
					System.out.println("Initial count: " + initialCount);
				} */
				
				//System.out.println("Size: " + ConnectionFilter.getInstance(0).getCurrentClients().size());
				//System.out.println("Initial count: " + initialCount);
				// END
				
				try {
					Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET online=1 WHERE user='" + user + "'");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				builder.setPlayer(Server.getServer().findSave(user, world), loginCode);
				world.registerPlayer(user, ip);
			} else {
				builder.setPlayer(null, loginCode);
			}
			LSPacket packet = builder.getPacket();
			if (packet != null) {
				session.write(packet);
			}
			session.getParent().disconnect(); // We should try closing the channel immediately as its terminated 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private byte validatePlayer(long user, String pass, String ip, World world) {
		Server server = Server.getServer();
		byte returnVal = 0;

		try {
			ResultSet result = Server.db.getQuery("SELECT `sub_expires`, `pass`, `salt`, `banned`, `owner`, `group_id` FROM `" + Config.MYSQL_TABLE_PREFIX + "players` WHERE `user`='" + user + "'");            if (!result.next()) {
				return 2;
			}

			String hashedPassword = DataConversions.hmac("SHA512", result.getString("salt") + pass, Config.HASH_PRIVATE_KEY);

			if (!hashedPassword.equals(result.getString("pass"))) {
				return 2;
			}

			if (result.getInt("banned") == 1) {
				System.out.println("Banned player: " + DataConversions.hashToUsername(user) + " trying to login.");
				return 6;
			}
			/*
            if (world.isMembers()) {
                long sub_expires = result.getLong("sub_expires");
                long difference = sub_expires - (System.currentTimeMillis() / 1000);

                if (difference <= 0) {
                    return 10;
                }
            }
			 */
			if (result.getInt("group_id") == 1 || result.getInt("group_id") == 2) {
				returnVal = 99;
			}

			int owner = result.getInt("owner");
			for (World w : server.getWorlds()) {
				for (Entry<Long, Integer> player : w.getPlayers()) {
					if (player.getKey() == user) {
						return 3;
					}
					if (player.getValue() == owner) {
						return 9;
					}
				}
				if (w.hasPlayer(user)) {
					return 3;
				}
			}
			return returnVal;
		} catch (SQLException e) {
			System.out.println("Exception in PlayerLoginHandler :" + e.getMessage());
			return 7;
		} catch(Exception e) {
			e.printStackTrace();
			return 7;
		}
		
	}
}
