package com.prsc.gs.plugins.shops;

import com.prsc.gs.model.InvItem;

import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Shop;
import com.prsc.gs.plugins.ScriptablePlug;
import com.prsc.gs.plugins.ShopInterface;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class ThessaliasClothes extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 50, new InvItem(182, 3), new InvItem(15, 12), new InvItem(16, 10), new InvItem(17, 10), new InvItem(191, 1), new InvItem(194, 5), new InvItem(195, 3), new InvItem(187, 2), new InvItem(183, 4), new InvItem(200, 5), new InvItem(807, 3), new InvItem(808, 3));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 59) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, true, "Hello");
			playMessages(p, n, false, "Do you want to buy any fine clothes?");
			
			/**
			 * I have lost my scythe can I get another please?
			 * 		Ohh you poor dear, I have another here
			 * 			'Thessalia gives you a new scythe'
			 * I have lost my bunny ears can I get some more please?
			 * 		Ohh you poor dear, I have another here
			 * 			'Thessalia gives you some new bunny ears'
			 * 
			 */
			String[] options = new String[] { "What have you got?", "No, thank you" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					
					switch (option) {
					case 0:
						owner.setAccessingShop(shop);
						owner.getActionSender().showShop(shop);
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
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 59;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return false;
	}

}
