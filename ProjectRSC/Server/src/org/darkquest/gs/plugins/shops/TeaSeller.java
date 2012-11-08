package org.darkquest.gs.plugins.shops;

import org.darkquest.config.Constants;
import org.darkquest.config.Formulae;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.ShopInterface;
import org.darkquest.gs.plugins.listeners.action.PickupListener;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.PickupExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.world.Shop;
import org.darkquest.gs.world.World;

public final class TeaSeller extends Scriptable implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener, PickupExecutiveListener, PickupListener {

	private final Shop shop = new Shop(false, 30000, 100, 50, new InvItem(739, 20));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 780) { // fucked
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Greetings!", "Are you in need of refreshment ?");
			
			String[] options = new String[] { "Yes please", "No thanks", "What are you selling?" };
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
						playMessages(owner, n, false, "Well if you're sure", "You know where to come if you do !");
						break;
					case 2:
						playMessages(owner, n, false, "Only the most delicious infusion", "Of the leaves of the tea plant", "Grown in the exotic regions of this world...", "Buy yourself a cup !");
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
		return n.getID() == 780;
	}

	@Override
	public boolean isMembers() {
		return true;
	}
	
	public Shop[] getShops() {
		return new Shop[] { shop };
	}

	@Override
	public void onPickup(Player p, Item i) {
		if (i.getID() == 1285) {
			p.setBusy(true);
			Npc n = World.getWorld().getNpcById(780);
			
			if (n == null) {
				return;
			} else if (Formulae.getDirection(p, n) != -1) {
			    n.setSprite(Formulae.getDirection(p, n));
			    p.setSprite(Formulae.getDirection(n, p));
			}
			
			playMessages(p, n, false, "hey ! get your hands off that tea !", "that's for display purposes only");
			
			p.setBusy(false);
		}
	}

	@Override
	public boolean blockPickup(Player p, Item i) {
		return i.getID() == 1285;
	}
	
}
