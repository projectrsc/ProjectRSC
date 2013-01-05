package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Shop;
import com.prsc.gs.model.component.mob.player.Inventory;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class ShopHandler implements PacketHandler {

	public void handlePacket(Packet p, Channel session) throws Exception {
		Client client = (Client) session.getAttachment();
		Player player = client.getPlayer();
		
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy()) {
			player.resetShop();
			return;
		}
		
		final Shop shop = player.getShop();
		
		if (shop == null) {
			player.setSuspiciousPlayer(true);
			player.resetShop();
			return;
		}
		
		int value = 0;
		InvItem item = null;

		switch (pID) {
		case 253: // Close shop
			player.resetShop();
			//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " closed shop window at: " + player.getLocation()));
			break;
		case 128: // Buy item
			Short s = p.readShort();
			item = new InvItem(s, 1);
			value = p.readInt();

			//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " tried to buy item " + s + " for " + value + " at " + player.getLocation()));

			if(value < shop.getItemBuyPrice(s)) {
				return;
			}
			if (shop.getItemCount(item.getID()) < 1)
				return;
			
			if (player.getInventory().countId(10) < value) {
				player.getActionSender().sendMessage("You don't have enough money!");
				return;
			} 
			
			if ((Inventory.MAX_SIZE - player.getInventory().size()) + player.getInventory().getFreedSlots(new InvItem(10, value)) < player.getInventory().getRequiredSlots(item)) {
				player.getActionSender().sendMessage("You can't hold the objects you are trying to buy!");
				return;
			}
			int itemprice = item.getDef().getBasePrice();
			int userHasToPay = shop.getItemBuyPrice(item.getID());

			if (itemprice == 0 || userHasToPay < itemprice)
				return;

			if (player.getInventory().remove(10, userHasToPay) > -1) {
				shop.removeShopItem(item);
				player.getInventory().add(item);
				player.getActionSender().sendSound("coins");
				player.getActionSender().sendInventory();
			}
			break;
		case 255: // Sell item
			Short s1 = p.readShort();
			item = new InvItem(s1, 1);
			value = p.readInt();

			//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " tried to sell item " + s1 + " for " + value + " at " + player.getLocation()));
			if (player.getInventory().countId(item.getID()) < 1) {
				return;
			} 
			if (!shop.shouldStock(item.getID())) {
				return;
			}
			if (!shop.canHoldItem(item)) {
				player.getActionSender().sendMessage("The shop is currently full!");
				return;
			}

			int itempricez;
			
			if (shop.getItemCount(item.getID()) == 0) {
				itempricez = value; //shop.getGenericPrice(item, false);
			} else {
				itempricez = shop.getItemSellPrice(item.getID());
			}
			
			if (itempricez > 300000)
				return;

			if (player.getInventory().remove(item) > -1) {
				player.getInventory().add(new InvItem(10, itempricez));
				shop.addShopItem(item);
				player.getActionSender().sendSound("coins");
				player.getActionSender().sendInventory();
			}
			break;
		}
	}

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{253, 128, 255};
	}
}