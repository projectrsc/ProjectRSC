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

public final class CassieShields extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 25000, 100, 50, new InvItem(4, 5), new InvItem(124, 3), new InvItem(128, 3), new InvItem(3, 2), new InvItem(2, 0),  new InvItem(125, 0), new InvItem(129, 0), new InvItem(126, 0));
	
	public static final int npcid = 101;
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
			
			playMessages(p, n, false, "Welcome to my shield shop", "do you wanna buy or sell some shields?");
			
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