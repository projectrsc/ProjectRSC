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

public final class RommikCrafts extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 5000, 100, 50, new InvItem(167, 2), new InvItem(293, 4), new InvItem(295, 2), new InvItem(294, 2), new InvItem(39, 3), new InvItem(43, 100), new InvItem(386, 3));
	
	public static final int npcid = 156;
	@Override
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == npcid || n.getID() == 173;
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == npcid || n.getID() == 173) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Welcome to my crafting shop", "Would you like to see my items?");
			
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