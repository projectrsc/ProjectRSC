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

public final class PeksaHelmets extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 50, new InvItem(104, 5), new InvItem(5, 3), new InvItem(105, 3), new InvItem(106, 1), new InvItem(107, 1), new InvItem(108, 4), new InvItem(6, 3), new InvItem(109, 2), new InvItem(110, 1), new InvItem(111, 1));
	
	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 75;
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 75) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Welcome to my helmet shop", "do you wanna buy or sell some helmets?");
			
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