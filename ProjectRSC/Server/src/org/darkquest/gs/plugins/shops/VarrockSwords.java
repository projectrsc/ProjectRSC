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

public final class VarrockSwords extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 110, 50, new InvItem(66, 5), new InvItem(1, 4), new InvItem(67, 4), new InvItem(424, 3), new InvItem(68, 3), new InvItem(69, 2), new InvItem(70, 4), new InvItem(71, 3), new InvItem(72, 3), new InvItem(425, 2), new InvItem(73, 2), new InvItem(74, 1), new InvItem(62, 10), new InvItem(28, 6), new InvItem(63, 5), new InvItem(423, 4), new InvItem(64, 3), new InvItem(65, 2));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 130 || n.getID() == 56 && p.getLocation().inBounds(133, 522, 138, 527)) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Hello bold adventurer", "Can I interest you in some swords?");
			
			String[] options = new String[] { "Yes please", "No, I'm OK for swords right now" };
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
						playMessages(owner, n, false, "Come back if you need any");
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
		if (n.getID() == 130 || n.getID() == 56) {
			if (p.getX() >= 133 && p.getX() <= 138 && p.getY() >= 522 && p.getY() <= 527) {
				return true;
			}
		}
		return false;
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
