package org.darkquest.gs.plugins.npcs;

import java.util.Random;

import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class FarmerMan extends Scriptable implements TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Random random = new Random();
	
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 11 || n.getID() == 63) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, true, "Hello", "How's it going?");
			
			switch (random.nextInt(5)) {
			case 0:
				playMessages(p, n, false, "Get out of my way", "I'm in a hurry");
				n.unblock();
				break;
			case 1:
				playMessages(p, n, false, "Not too bad");
				n.unblock();
				break;
			case 2:
				playMessages(p, n, false, "I'm fine", "How are you?");
				playMessages(p, n, true, "Very well, thank you");
				n.unblock();
				break;
			case 3:
				playMessages(p, n, false, "Hello", "Nice weather we've been having");
				n.unblock();
				break;
			case 4:
				playMessages(p, n, false, "How can I help you?");
				
				String[] options = new String[] { "Do you wish to trade?", "I'm in search of quest", "I'm in search of enemies to kill" };
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);
						
						switch (option) {
						case 0:
							playMessages(owner, n, false, "No I have nothing I wish to get rid of", "If you want to do some trading,", "there are plenty of shops and market stalls around though");
							break;
						case 1:
							playMessages(owner, n, false, "I'm sorry I can't help you there");
							break;
						case 2:
							playMessages(owner, n, false, "I've heard there are many fearsome creatures under the ground");
							break;
						}
						owner.setBusy(false);
						n.unblock();
					}
				});
				p.getActionSender().sendMenu(options);
				break;
			}

			p.setBusy(false);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 11 || n.getID() == 63;
	}

}
