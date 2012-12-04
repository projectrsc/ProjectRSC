package com.prsc.ls.packethandler.loginserver;


import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.World;
import com.prsc.ls.net.LSPacket;
import com.prsc.ls.net.Packet;
import com.prsc.ls.net.monitor.Monitor;
import com.prsc.ls.packetbuilder.loginserver.PlayerLoginPacketBuilder;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.util.Config;
import com.prsc.ls.util.DataConversions;

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
			
			/*
			if(Monitor.getInstance() != null && Monitor.getInstance().hasMaxedLimit(DataConversions.IPToLong(ip))) {
				loginCode = 8;
			} */
			
			builder.setUID(uID);
			if (loginCode == 0 || loginCode == 1 || loginCode == 99) {//
				
				if(Monitor.getInstance() != null) {
					Monitor.getInstance().onLogin(session);
				}
				
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
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private byte validatePlayer(long user, String pass, String ip, World world) {
		Server server = Server.getServer();
		byte returnVal = 0;

		try {
			ResultSet result = Server.db.getQuery("SELECT `sub_expires`, `pass`, `salt`, `banned`, `owner`, `group_id` FROM `" + Config.MYSQL_TABLE_PREFIX + "players` WHERE `user`='" + user + "'");            
			if (!result.next()) {
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
