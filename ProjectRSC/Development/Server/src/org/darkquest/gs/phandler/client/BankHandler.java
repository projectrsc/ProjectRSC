package org.darkquest.gs.phandler.client;

import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.connection.RSCPacket;
import org.darkquest.gs.external.EntityHandler;
import org.darkquest.gs.model.Bank;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Inventory;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

public final class BankHandler implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy() || player.isRanging() || player.isTrading() || player.isDueling()) {
			player.resetBank();
			return;
		}
		if (!player.accessingBank()) {
			player.setSuspiciousPlayer(true);
			player.resetBank();
			return;
		}
		Bank bank = player.getBank();
		Inventory inventory = player.getInventory();
		InvItem item;
		int itemID, amount, slot;
		switch (pID) {
		case 48: // Close bank
		player.resetBank();
		//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " closed their bank"));
		break;
		case 198: // Deposit item
			itemID = p.readShort();
			amount = p.readInt();

			if (amount < 1 || inventory.countId(itemID) < amount) {
				player.setSuspiciousPlayer(true);
				return;
			}
			if (PluginHandler.getPluginHandler().blockDefaultAction("Deposit", new Object[]{player, itemID, amount})) {
				return;
			}

			//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " deposited item " + itemID + " amount " + amount));

			if (EntityHandler.getItemDef(itemID).isStackable()) {
				item = new InvItem(itemID, amount);
				if (bank.canHold(item) && inventory.remove(item) > -1) {
					bank.add(item);
				} else {
					player.getActionSender().sendMessage("You don't have room for that in your bank");
				}
			} else {
				for (int i = 0; i < amount; i++) {
					int idx = inventory.getLastIndexById(itemID);
					item = inventory.get(idx);
					if (item == null) { // This shouldn't happen
						break;
					}
					if (bank.canHold(item) && inventory.remove(item) > -1) {
						bank.add(item);
					} else {
						player.getActionSender().sendMessage("You don't have room for that in your bank");
						break;
					}
				}
			}
			slot = bank.getFirstIndexById(itemID);
			if (slot > -1) {
				player.getActionSender().sendInventory();
				player.getActionSender().updateBankItem(slot, itemID, bank.countId(itemID));
			}
			break;
		case 183: // Withdraw item
			itemID = p.readShort();
			amount = p.readInt();
			if (amount < 1 || bank.countId(itemID) < amount) {
				player.setSuspiciousPlayer(true);
				return;
			}

			if (PluginHandler.getPluginHandler().blockDefaultAction("Withdraw", new Object[]{player, itemID, amount})) {
				return;
			}

			//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " withdrew item " + itemID + " amount " + amount));

			slot = bank.getFirstIndexById(itemID);
			if (EntityHandler.getItemDef(itemID).isStackable()) {
				item = new InvItem(itemID, amount);
				if (inventory.canHold(item) && bank.remove(item) > -1) {
					inventory.add(item);
				} else {
					player.getActionSender().sendMessage("You don't have room for that in your inventory");
				}
			} else {
				for (int i = 0; i < amount; i++) {
					if (bank.getFirstIndexById(itemID) < 0) {
						break;
					}
					item = new InvItem(itemID, 1);
					if (inventory.canHold(item) && bank.remove(item) > -1) {
						inventory.add(item);
					} else {
						player.getActionSender().sendMessage("You don't have room for that in your inventory");
								break;
					}
				}
			}
			if (slot > -1) {
				player.getActionSender().sendInventory();
				player.getActionSender().updateBankItem(slot, itemID, bank.countId(itemID));
			}
			break;
		}
	}

}