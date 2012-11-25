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

public final class ChampionsGuild extends ScriptablePlug implements ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop scavvosShop = new Shop(false, 300000, 300, 50, new InvItem(406, 1), new InvItem(402, 1), new InvItem(93, 1), new InvItem(400, 1), new InvItem(75, 1), new InvItem(397, 1));
	private final Shop valsShop = new Shop(false, 60000, 300, 50, new InvItem(229, 2), new InvItem(230, 1), new InvItem(248, 1), new InvItem(120, 1));
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		p.setBusy(true);
		n.blockedBy(p);
		
		String[] options = null;
		
		switch(n.getID()) {
		case 183: // scavvo
			if(p.getUsername().equalsIgnoreCase("fenex") || p.getUsername().equalsIgnoreCase("drizzy")) {
				playMessages(p, n, false, "ello matey", "Want to buy some exciting new sex toys?");
			} else if(p.getUsername().equalsIgnoreCase("swo0osh")) { 
				playMessages(p, n, false, "ello matey", "ooh ooh wanna try some toys??");
			} else {
				playMessages(p, n, false, "ello matey", "Want to buy some exciting new toys?");
			}
				
			options = new String[] { "No, toys are for kids", "Lets have a look then", "Ooh goody goody toys" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
						
					switch (option) {
					case 0: 
						break;
					case 1:
					case 2:
						owner.setAccessingShop(scavvosShop);
						owner.getActionSender().showShop(scavvosShop);
						break;
					}
					owner.setBusy(false);
					n.unblock();
				}
			});
			break;
		case 112: // valaerie
			playMessages(p, n, false, "Hello there", "Want to have a look at what we're selling today?");
			
			options = new String[] { "Yes please", "No thank you" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
						
					switch (option) {
					case 0: 
						owner.setAccessingShop(valsShop);
						owner.getActionSender().showShop(valsShop);
						break;
					case 1:
						break;
					}
					owner.setBusy(false);
					n.unblock();
				}
			});
			break;
		}
		
		p.getActionSender().sendMenu(options);
		p.setBusy(false);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 183 || n.getID() == 112;
	}

	@Override
	public Shop[] getShops() {
		return new Shop[] { scavvosShop, valsShop };
	}

	@Override
	public boolean isMembers() {
		return false;
	}


}

