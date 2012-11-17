package org.darkquest.gs.plugins.shops;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.ScriptablePlug;
import org.darkquest.gs.plugins.ShopInterface;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.world.Shop;

public final class BobsAxes extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 15000, 100, 50, new InvItem(156, 5), new InvItem(87, 10), new InvItem(12, 5), new InvItem(88, 3), new InvItem(89, 5), new InvItem(90, 2), new InvItem(91, 1));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 1) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Hello. How can I help you?");
			
			String[] options = new String[] { "Give me a quest!", "Have you anything to sell?" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					
					switch (option) {
					case 0:
						playMessages(owner, n, false, "Get yer own!");
						break;
					case 1:
						playMessages(owner, n, false, "Yes, I buy and sell axes, take your pick! (or axe)");
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
		return n.getID() == 1;
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
