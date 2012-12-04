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

public final class BriansBattleAxes extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 15000, 100, 50, new InvItem(205, 4), new InvItem(89, 3), new InvItem(90, 2), new InvItem(429, 1), new InvItem(91, 1), new InvItem(92, 1));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 131) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "ello");
			
			String[] options = new String[] { "So are you selling something?", "ello" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					
					switch (option) {
					case 0:
						playMessages(owner, n, false, "Yep take a look at these great axes");
						
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
		return n.getID() == 131;
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
