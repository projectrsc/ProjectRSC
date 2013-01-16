package com.prsc.gs.plugins.shops;

import com.prsc.config.Formulae;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Shop;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.ScriptablePlug;
import com.prsc.gs.plugins.ShopInterface;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.action.WallObjectActionListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.prsc.gs.plugins.listeners.executive.WallObjectActionExecutiveListener;

public final class WydinsGrocery extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener, WallObjectActionExecutiveListener, WallObjectActionListener {

	private final Shop shop = new Shop(false, 12500, 100, 50, new InvItem(136, 3), new InvItem(133, 1), new InvItem(18, 3), new InvItem(249, 3), new InvItem(236, 1), new InvItem(138, 0), new InvItem(337, 1), new InvItem(319, 3), new InvItem(320, 3), new InvItem(348, 1));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 129) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "welcome to my foodstore", "would you like to buy anything");
			
			String[] options = new String[] { "yes please", "No thankyou", "what can you recommend?" };
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
						break;
					case 2:
						playMessages(owner, n, false, "we have this really exotic fruit", "all the way from Karamja", "it's called a banana");
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
		return n.getID() == 129;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 47 && obj.getX() == 277 && obj.getY() == 658) {
			final Npc n = World.getWorld().getNpcById(129);
			
			if (n != null) {
				p.setBusy(true);
				n.blockedBy(p);
				
				if (Formulae.getDirection(p, n) != -1) {
				    n.setSprite(Formulae.getDirection(p, n));
				    p.setSprite(Formulae.getDirection(n, p));
				}
				
				playMessages(p, n, false, "heh you can't go in there", "only employees of the grocery store can go in");
				
				String[] options = new String[] { "Well can I get a job here?", "Sorry I didn't realise" };
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);
						
						if (option == 0) {
							playMessages(owner, n, false, "well you can't work here unless you have an apron", "health and safety regulations, you understand");
						}
						owner.setBusy(false);
						n.unblock();
					}
				});
				p.getActionSender().sendMenu(options);
				p.setBusy(false);
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return obj.getID() == 47 && obj.getX() == 277 && obj.getY() == 658;
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
