package org.darkquest.gs.plugins.npcs;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class WysonTheGardener extends Scriptable implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 116;
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 116) {
			p.setBusy(true);
			n.blockedBy(p);

			playMessages(p, n, false, "i am the gardener round here", "do you have any gardening that needs doing?");

			String[] options = new String[] { "I'm looking for woad leaves", "Not right now thanks" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);

					switch (option) {
					case 0:
						playMessages(owner, n, false, "well luckily for you i may have some around here somewhere");
						playMessages(owner, n, true, "can i buy one please?");
						playMessages(owner, n, false, "how much are you willing to pay?");

						String[] options = new String[] { "How about 5 coins?", "How about 10 coins?", "How about 15 coins?", "How about 20 coins?" };
						owner.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);

								switch (option) {
								case 0:
								case 1:
									playMessages(owner, n, false, "no no thats far too little. woad leaves are hard to get you know", "i used to have plenty but someone kept stealing them off me");
									break;
								case 2:
								case 3:
									playMessages(owner, n, false, (option == 2 ? "mmmm ok that sounds fair." : "ok that's more than fair."));

									if (owner.getInventory().countId(10) >= (option * 5) + 5) {
										owner.getActionSender().sendMessage("you give wyson " + ((option * 5) + 5) + " coins");
										owner.getActionSender().sendMessage("wyson the gardener gives you some woad leaves");

										owner.getInventory().remove(10, (option * 5) + 5);
										owner.getInventory().add(new InvItem(281, 1));
										owner.getActionSender().sendInventory();
									} else {
										playMessages(owner, n, true, "i dont have enough coins to buy the leaves. i'll come back later");
									}
									break;
								}
								owner.setBusy(false);
								n.unblock();
							}
						});
						owner.getActionSender().sendMenu(options);
						break;
					case 1:
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

}
