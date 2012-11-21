package org.darkquest.gs.phandler;

import org.darkquest.config.Constants;

import org.darkquest.config.Formulae;
import org.darkquest.gs.builders.MiscPacketBuilder;
import org.darkquest.gs.builders.RSCPacketBuilder;
import org.darkquest.gs.connection.filter.ConnectionFilter;
import org.darkquest.gs.model.*;
import org.darkquest.gs.phandler.client.WieldHandler;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


public final class PlayerLogin implements PacketHandler {

	private static final World world = World.getWorld();

	private final Player player;

	public PlayerLogin(Player player) {
		this.player = player;
	}

	public void handlePacket(org.darkquest.gs.connection.Packet p, Channel session) throws Exception {		
		byte loginCode = p.readByte();	
		
		RSCPacketBuilder pb = new RSCPacketBuilder();
		pb.setBare(true);
		pb.addByte(loginCode);
		player.getSession().write(pb.toPacket());
		
		try {
			if (loginCode == 0 || loginCode == 1 || loginCode == 99) {	
				player.setOwner(p.readInt());

				player.setGroupID(p.readInt());

				player.setSubscriptionExpires(p.readLong());

				player.setLastIP(DataConversions.IPToString(p.readLong()));
				player.setLastLogin(p.readLong());
				
				if (player.getLastLogin() == 0L) {
					player.setLocation(Point.location(121, 647), true);
					p.readShort();
					p.readShort();
				}
				else {
					player.setLocation(Point.location(p.readShort(), p.readShort()), true);
				}

				player.setFatigue(p.readShort());
				player.incQuestPoints(p.readShort());
				player.setBotDetected(p.readByte() == 1);

				player.setCombatStyle((int) p.readByte());

				player.setPrivacySetting(0, p.readByte() == 1);
				player.setPrivacySetting(1, p.readByte() == 1);
				player.setPrivacySetting(2, p.readByte() == 1);
				player.setPrivacySetting(3, p.readByte() == 1);

				player.setGameSetting(0, p.readByte() == 1);
				player.setGameSetting(2, p.readByte() == 1);
				player.setGameSetting(3, p.readByte() == 1);
				player.setGameSetting(4, p.readByte() == 1);
				player.setGameSetting(5, p.readByte() == 1);
				player.setGameSetting(6, p.readByte() == 1);

				// Create the player's appearance. If it is invalid, destroy the player,
				// close their connection, and discontinue further processing - Lothy
				PlayerAppearance appearance = new PlayerAppearance(p.readShort(), p.readShort(), p.readShort(), p.readShort(), p.readShort(), p.readShort());
				if (!appearance.isValid()) {
					player.destroy(true);
					player.getSession().close();
					return;
				}
				player.setAppearance(appearance);
				player.setWornItems(player.getPlayerAppearance().getSprites());

				player.setMale(p.readByte() == 1);
				long skull = p.readLong();
				if (skull > 0)
					player.addSkull(skull);

				for (int i = 0; i < 18; i++) {
					int exp = (int) p.readLong();
					player.setExp(i, exp);
					player.setMaxStat(i, Formulae.experienceToLevel(exp));
					player.setCurStat(i, p.readShort());
				}

				player.setCombatLevel(Formulae.getCombatlevel(player.getMaxStats()));

				Inventory inventory = new Inventory(player);
				int invCount = p.readShort();
				for (int i = 0; i < invCount; i++) {
					InvItem item = new InvItem(p.readShort(), p.readInt());
					//System.out.println(item.toString());
					if (p.readByte() == 1 && item.isWieldable()) {
						item.setWield(true);
						player.updateWornItems(item.getWieldableDef().getWieldPos(), item.getWieldableDef().getSprite());
					}
					inventory.add(item);
				}

				player.setInventory(inventory);

				Bank bank = new Bank();

				int bnkCount = p.readShort();
				for (int i = 0; i < bnkCount; i++) {
					bank.add(new InvItem(p.readShort(), p.readInt()));
				}
				player.setBank(bank);


				try {

					int friendCount = p.readShort();
					for (int i = 0; i < friendCount; i++) {
						player.addFriend(p.readLong(), p.readShort());	
					}
				

				int ignoreCount = p.readShort();
				for (int i = 0; i < ignoreCount; i++) {
					player.addIgnore(p.readLong());
				}
				} catch(Exception e) {
					e.printStackTrace();
				}

				player.setMuteTime(p.readLong());

				int quests = p.readInt();
				for (int i = 0; i < quests; i++) {
					player.setQuestStage(p.readInt(), p.readInt());
				}
				//				int cacheSize = p.readInt();
				//				for (int i = 0; i < cacheSize; i++) {
				//					/**
				//					 * Construct key
				//					 */
				//					 int keyLenght = p.readInt();
				//					String key = new String(p.readBytes(keyLenght));
				//					/**
				//					 * Manage item type now
				//					 * 0 - Integer
				//					 * 1 - String
				//					 * 2 - Boolean
				//					 * 3 - Long
				//					 */
				//					 int identifier = p.readInt();
				//					if (identifier == 0) {
				//						player.getCache().store(key, p.readInt());
				//					}
				//					if (identifier == 1) {
				//						int stringLength = p.readInt();
				//						String string = new String(p.readBytes(stringLength));
				//						player.getCache().store(key, string);
				//					}
				//					if (identifier == 2) {
				//						player.getCache().store(key, (p.readInt() == 1));
				//					}
				//					if (identifier == 3) {
				//						player.getCache().store(key, p.readLong());
				//					}
				//				}

				/* Send client data */
				world.registerPlayer(player);

				player.updateViewedPlayers();
				player.updateViewedObjects();

				MiscPacketBuilder sender = player.getActionSender();

				sender.sendFatigue(player.getFatigue() / 10);
				sender.sendWorldInfo();
				sender.sendEquipmentStats();
				sender.sendStats();
				sender.sendPrivacySettings();
				sender.sendGameSettings();
				sender.sendFriendList();
				sender.sendIgnoreList();
				sender.sendCombatStyle();
				sender.sendQuestPoints();

				try {
					for (InvItem i : player.getInventory().getItems()) {
						if (i.getDef().isMembers() && !Constants.GameServer.MEMBER_WORLD) {
							WieldHandler.unWieldItem(player, i, true);
						}
					}
					sender.sendInventory();
				} catch (Exception e) {
					e.printStackTrace();
				}

				int timeTillShutdown = World.getWorld().getServer().timeTillShutdown();
				if (timeTillShutdown > -1)
					sender.startShutdown((int) (timeTillShutdown / 1000));
				
				// We have a new character being created...
				if (player.getLastLogin() == 0L && player.getInventory().size() == 0) {
					for(InvItem item : Player.STARTER_ITEMS) {
						player.getInventory().add(item);
					}
					sender.sendInventory();
					
					player.setChangingAppearance(true);
					sender.sendAppearanceScreen();
				}

				sender.sendWakeUp(false, true);
				sender.sendLoginBox();
				sender.sendMessage("@que@" + Constants.GameServer.MOTD);
				sender.sendQuestInfo();

				if (player.isAdmin()) {
					player.setnopk(true);
					player.setnonaggro(true);
				}

				player.setLoggedIn(true);
				player.setBusy(false);
				
				if(ConnectionFilter.getInstance() != null) {
					ConnectionFilter.getInstance().processCleanLogin(session, false);
				}

				PluginHandler.getPluginHandler().handleAction("PlayerLogin", new Object[]{player});
			} else {
				System.out.println("Not clean login");
				if(ConnectionFilter.getInstance() != null) {
					ConnectionFilter.getInstance().processCleanLogin(session, true);
				}
				player.destroy(true);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
