package org.darkquest.gs.plugins.npcs;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Bartender extends Scriptable implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 12) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "What can I do yer for?");
			
			String[] options = new String[] { "A glass of your finest ale please", "Can you recommend anywhere an adventurer might make his fortune?", "Do you know where I can get some good equipment?" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					
					switch (option) {
					case 0:
						playMessages(owner, n, false, "No problemo", "That'll be 2 coins");
						if (owner.getInventory().remove(10, 2) > -1) {
							owner.getActionSender().sendMessage("You buy a pint of beer");
							owner.getInventory().add(new InvItem(193, 1));
							owner.getActionSender().sendInventory();
						} else {
							playMessages(owner, n, true, "oh dear i don't seem to have enough coins");
						}
						n.unblock();
						break;
					case 1:
						playMessages(owner, n, false, "Ooh I don't know if I should be giving away information", "Makes the computer game too easy");
						String[] options = new String[] { "Oh ah well", "Computer game? What are you talking about?", "Just a small clue?" };
						owner.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);
								
								switch (option) {
								case 1:
									playMessages(owner, n, false, "This world around us..", "is all a computer game..", "called RuneScape");
									playMessages(owner, n, true, "Nope, still don't understand what you are talking about", "What's a computer?");
									playMessages(owner, n, false, "It's a sort of magic box thing.", "which can do all sorts of different things");
									playMessages(owner, n, true, "I give up", "You're obviously completely mad!");
									break;
								case 2:
									playMessages(owner, n, false, "Go and talk to the bartender in the Jolly Boar Inn", "He doesn't seem to mind giving away clues");
									break;
								}
								owner.setBusy(false);
								n.unblock();
							}
						});
						owner.getActionSender().sendMenu(options);
						break;
					case 2:
						playMessages(owner, n, false, "Well, there's the sword shop across the road.", "or there's also all sorts of shops up around the market");
						n.unblock();
						break;
					}
					owner.setBusy(false);
				}
			});
			p.getActionSender().sendMenu(options);
			p.setBusy(false);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 12;
	}

}
