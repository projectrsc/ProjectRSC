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

public final class FancyClothes extends Scriptable implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 110, 50, new InvItem(192, 0), new InvItem(185, 3), new InvItem(512, 1), new InvItem(541, 3), new InvItem(146, 3), new InvItem(39, 3), new InvItem(43, 100), new InvItem(16, 10), new InvItem(17, 10), new InvItem(807, 3), new InvItem(808, 3), new InvItem(191, 1), new InvItem(194, 5), new InvItem(195, 3), new InvItem(187, 2), new InvItem(183, 4), new InvItem(609, 3));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 501) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Now you look like someone who goes to a lot of fancy dress parties");
			playMessages(p, n, true, "Errr... what are you saying exactly?");
			playMessages(p, n, false, "I'm just saying that perhaps you would like to peruse my selection of garments");
			
			String[] options = new String[] { "I think I might leave the perusing for now thanks", "OK,lets see what you've got then" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					
					switch (option) {
					case 0:
						break;
					case 1:
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
		return n.getID() == 501;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean isMembers() {
		return true;
	}

}
