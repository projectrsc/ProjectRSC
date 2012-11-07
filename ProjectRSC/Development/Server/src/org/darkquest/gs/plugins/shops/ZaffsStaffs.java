package org.darkquest.gs.plugins.shops;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.ShopInterface;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.world.Shop;

public final class ZaffsStaffs extends Scriptable implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 50, new InvItem(614, 5), new InvItem(100, 5), new InvItem(198, 5), new InvItem(101, 2), new InvItem(102, 2), new InvItem(103, 2), new InvItem(197, 2));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 69) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "would you like to buy or sell some staffs?");
			
			String[] options = new String[] { "Yes please", "No, thank you" };
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
		return n.getID() == 69;
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
