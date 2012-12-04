package com.prsc.gs.plugins.shops;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.ScriptablePlug;
import com.prsc.gs.plugins.ShopInterface;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.prsc.gs.world.Shop;

public final class GrumsGoldShop extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 50, new InvItem(283, 0), new InvItem(284, 0), new InvItem(285, 0), new InvItem(286, 0), new InvItem(287, 0), new InvItem(288, 0), new InvItem(289, 0), new InvItem(290, 0), new InvItem(291, 0), new InvItem(292, 0), new InvItem(301, 0), new InvItem(302, 0));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 157) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Would you like to buy or sell some gold jewellery");
			
			String[] options = new String[] { "Yes please", "No, I'm not that rich" };
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
					case 1:
						playMessages(owner, n, false, "Get out then we don't want any riff-raff in here");
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
		return n.getID() == 157;
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
