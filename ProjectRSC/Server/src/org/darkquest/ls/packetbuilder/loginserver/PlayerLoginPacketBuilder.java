package org.darkquest.ls.packetbuilder.loginserver;

import org.darkquest.ls.Server;
import org.darkquest.ls.model.BankItem;
import org.darkquest.ls.model.InvItem;
import org.darkquest.ls.model.PlayerSave;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.LSPacket;
import org.darkquest.ls.util.Config;
import org.darkquest.ls.packetbuilder.LSPacketBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public class PlayerLoginPacketBuilder {
	/**
	 * Players Login Code
	 */
	private byte loginCode;
	/**
	 * Players Saved Data
	 */
	private PlayerSave save;
	/**
	 * Packets uID
	 */
	private long uID;

	public LSPacket getPacket() {
		Server server = Server.getServer();

		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addByte(loginCode);
		if (save != null) {
			packet.addInt(save.getOwner());
			packet.addInt(save.getGroup());

			packet.addLong(save.getSubscriptionExpires());

			packet.addLong(save.getLastIP());
			packet.addLong(save.getLastLogin());

			packet.addShort(save.getX());
			packet.addShort(save.getY());

			packet.addShort(save.getFatigue());
			packet.addShort(save.getQuestPoints());
			packet.addByte((byte) (save.botDetected() ? 1 : 0));

			packet.addByte(save.getCombatStyle());

			packet.addByte((byte) (save.blockChat() ? 1 : 0));
			packet.addByte((byte) (save.blockPrivate() ? 1 : 0));
			packet.addByte((byte) (save.blockTrade() ? 1 : 0));
			packet.addByte((byte) (save.blockDuel() ? 1 : 0));

			packet.addByte((byte) (save.cameraAuto() ? 1 : 0));
			packet.addByte((byte) (save.oneMouse() ? 1 : 0));
			packet.addByte((byte) (save.soundOff() ? 1 : 0));
			packet.addByte((byte) (save.showRoof() ? 1 : 0));
			packet.addByte((byte) (save.autoScreenshot() ? 1 : 0));
			packet.addByte((byte) (save.combatWindow() ? 1 : 0));

			packet.addShort(save.getHairColour());
			packet.addShort(save.getTopColour());
			packet.addShort(save.getTrouserColour());
			packet.addShort(save.getSkinColour());
			packet.addShort(save.getHeadSprite());
			packet.addShort(save.getBodySprite());

			packet.addByte((byte) (save.isMale() ? 1 : 0));
			packet.addLong(save.getSkullTime());

			for (int i = 0; i < 18; i++) {
				packet.addLong(save.getExp(i));
				packet.addShort(save.getStat(i));
			}

			int invCount = save.getInvCount();
			packet.addShort(invCount);
			for (int i = 0; i < invCount; i++) {
				InvItem item = save.getInvItem(i);
				packet.addShort(item.getID());
				packet.addInt(item.getAmount());
				packet.addByte((byte) (item.isWielded() ? 1 : 0));
			}

			int bankCount = save.getBankCount();
			packet.addShort(bankCount);
			for (int i = 0; i < bankCount; i++) {
				BankItem item = save.getBankItem(i);
				packet.addShort(item.getID());
				packet.addInt(item.getAmount());
			}

			ArrayList<Long> friendsWithUs = new ArrayList<Long>();
			try {
				ResultSet result = Server.db.getQuery("SELECT p.user FROM `" + Config.MYSQL_TABLE_PREFIX + "friends` AS f INNER JOIN `" + Config.MYSQL_TABLE_PREFIX + "players` AS p ON p.user=f.friend WHERE p.block_private=0 AND f.user='" + save.getUser() + "'");
				while (result.next()) {
					friendsWithUs.add(result.getLong("user"));
				}
				result = Server.db.getQuery("SELECT user FROM `" + Config.MYSQL_TABLE_PREFIX + "friends` WHERE friend='" + save.getUser() + "'");
				while (result.next()) {
					friendsWithUs.add(result.getLong("user"));
				}
			} catch (SQLException e) {
				Server.error(e);
			}

			try {
				int friendCount = save.getFriendCount();
			//	if(friendCount > 45)
					//friendCount = 45;
				packet.addShort(friendCount);
				
				for (int i = 0; i < friendCount; i++) {
					
					long friend = save.getFriend(i);
					World world = server.findWorld(friend);
					packet.addLong(friend);
					packet.addShort(world == null || !friendsWithUs.contains(friend) ? 0 : world.getID());
				}


				int ignoreCount = save.getIgnoreCount();
				//if(ignoreCount > 49)
				//	ignoreCount = 49;
				packet.addShort(ignoreCount);
				for (int i = 0; i < ignoreCount; i++) {
					packet.addLong(save.getIgnore(i));
				}

				packet.addLong(save.getMuted());

				Set<Integer> quests = save.getQuestIDs();
				packet.addInt(quests.size());
				for (int id : save.getQuestIDs()) {
					packet.addInt(id);
					packet.addInt(save.getQuestStage(id));
				}

			} catch(Exception e) {
				e.printStackTrace();
			}


			//            Map<String, Object> cache = save.getCache();
			//            /**
			//             * Send the size of the cache to the login server
			//             */
			//            packet.addInt(cache.size());
			//            /**
			//             * first Integer identifies the type.
			//             * 0 - Integer
			//             * 1 - String
			//             * 2 - Boolean
			//             * 3 - Long
			//             */
			//            for (String key : cache.keySet()) {
			//                byte[] data = key.getBytes();
			//                packet.addInt(data.length);
			//                packet.addBytes(data);
			//
			//                Object o = cache.get(key);
			//                if (o instanceof Integer) {
			//                    packet.addInt(0);
			//                    packet.addInt((Integer) o);
			//                }
			//                if (o instanceof String) {
			//                    packet.addInt(1);
			//                    data = ((String) o).getBytes();
			//                    packet.addInt(data.length);
			//                    packet.addBytes(data);
			//                }
			//                if (o instanceof Boolean) {
			//                    packet.addInt(2);
			//                    packet.addInt((Boolean) o ? 1 : 0);
			//                }
			//                if (o instanceof Long) {
			//                    packet.addInt(3);
			//                    packet.addLong((Long) o);
			//                }
			//            }
		}
		return packet.toPacket();
	}

	/**
	 * Sets the packet to reply to
	 */
	public void setPlayer(PlayerSave save, byte loginCode) {
		this.save = save;
		this.loginCode = loginCode;
	}

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}
}
