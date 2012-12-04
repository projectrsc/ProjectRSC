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

public final class GerrantsFishingGear extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 12000, 100, 50, new InvItem(376, 5), new InvItem(377, 5), new InvItem(378, 5), new InvItem(379, 2), new InvItem(375, 2), new InvItem(380, 200), new InvItem(381, 200), new InvItem(349, 30), new InvItem(354, 0), new InvItem(361, 0), new InvItem(351, 0), new InvItem(358, 0), new InvItem(363, 0), new InvItem(356, 0), new InvItem(366, 0), new InvItem(372, 0), new InvItem(369, 0));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 167) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Welcome you can buy any fishing equipment at my store", "We'll also buy anything you catch off you");
			
			String[] options = new String[] { "Let's see what you've got then", "Sorry, I'm not interested" };
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
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 167;
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
