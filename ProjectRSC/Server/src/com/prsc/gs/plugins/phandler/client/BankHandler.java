package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.model.component.mob.player.Bank;
import com.prsc.gs.model.component.mob.player.Inventory;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class BankHandler implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel channel) throws Exception {
		Client client = (Client) channel.getAttachment();
		Player player = client.getPlayer();
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

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{48, 183, 198};
	}

}