package com.prsc.gs.plugins.shops;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Point;
import com.prsc.gs.plugins.ScriptablePlug;
import com.prsc.gs.plugins.ShopInterface;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.prsc.gs.world.Shop;

public final class GeneralStore extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private Shop baseShop = new Shop(true, 12400, 100, 40, new InvItem(135, 3), new InvItem(140, 2), new InvItem(144, 2), new InvItem(21, 2), new InvItem(166, 2), new InvItem(167, 2), new InvItem(168, 5), new InvItem(1263, 10));

	
	public static InvItem[] shop_items = new InvItem[] {new InvItem(135, 3), new InvItem(140, 2), new InvItem(144, 2), new InvItem(21, 2), new InvItem(166, 2), new InvItem(167, 2), new InvItem(168, 5), new InvItem(1263, 10)};
	private Shop[] shops = null;
	

	
	
	@Override
	public Shop[] getShops() {
		if(shops == null) {
			shops = new Shop[8];
			shops[0] = new Shop(baseShop, "Dwarven Mine", 143);
			shops[1] = new Shop(baseShop, "Varrock", 105, 106);
			shops[2] = new Shop(baseShop, "Falador", 106, 106);
			shops[3] = new Shop(baseShop, "Lumbridge", 83, 55);
			shops[4] = new Shop(baseShop, "Rimmington", 82);
			shops[5] = new Shop(baseShop, "Karamja", 168, 169);
			shops[6] = new Shop(baseShop, "Al_Kharid", 88, 87);
			shops[7] = new Shop(baseShop, "Edgeville", 186, 185);
				

		}
		return shops;
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		//if (n.getID() == 105 || n.getID() == 106 || n.getID() == 55 || n.getID() == 83 || n.getID() == 186 || n.getID() == 185 || n.getID() == 169 || n.getID() == 168 || n.getID() == 82 || n.getID() == 143 || n.getID() == 88 || n.getID() == 87) {
			//return true;
		//}
		for(Shop s : shops) {
			if(s != null) {
				for(int i : s.ownerIDs) {
					if(i == n.getID())
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		boolean found = false;
		Shop shp = null;
		for(Shop s : shops) {
			if(s != null) {
				for(int i : s.ownerIDs) {
					if(i == n.getID()) {
						found = true;
						shp = s;
					}
				}
			}
		}
		if(!found)
			return;
		
		final Shop shap = shp;
		
		if (found) {
			p.setBusy(true);
			n.blockedBy(p);

			playMessages(p, n, false, "Can I help you at all?");

			String[] options = new String[] { "Yes please. what are you selling?", "No thanks" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);

					switch (option) {
					case 0:
						playMessages(owner, n, false, "Take a look");
						Point location = owner.getLocation();
						
						Shop shop = shap;
						
						
						if (location.getX() >= 132 && location.getX() <= 137 && location.getY() >= 639 && location.getY() <= 644) {
							shop = shops[3];
						} else if (location.getX() >= 317 && location.getX() <= 322 && location.getY() >= 530 && location.getY() <= 536) {
							shop = shops[2];
						} else if (location.getX() >= 124 && location.getX() <= 129 && location.getY() >= 513 && location.getY() <= 518) {
							shop = shops[1];
						}
						if (shop != null) {
							owner.setAccessingShop(shop);
							owner.getActionSender().showShop(shop);
						}
						break;
					}
					owner.setBusy(false);
					n.unblock();
				}
			});
			p.getActionSender().sendMenu(options);
			p.setBusy(false);
		}
	}

	@Override
	public boolean isMembers() {
		return false;
	}

}
