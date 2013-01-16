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

public final class GemTrader extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 50, new InvItem(160, 1), new InvItem(159, 1), new InvItem(158, 0), new InvItem(157, 0), new InvItem(164, 1), new InvItem(163, 1), new InvItem(162, 0), new InvItem(161, 0));
	
	public static final int npcid = 308;
	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == npcid;
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == npcid) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Welcome to my gem shop", "do you wanna buy or sell some gems?");
			
			String[] options = new String[] { "Yes please", "No thanks" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					
					if (option == 0) {
						owner.setAccessingShop(shop);
						owner.getActionSender().showShop(shop);
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