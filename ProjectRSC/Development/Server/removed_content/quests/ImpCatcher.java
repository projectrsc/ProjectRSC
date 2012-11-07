package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class ImpCatcher extends Quest implements TalkToNpcListener, TalkToNpcExecutiveListener {
	
	private final InvItem AMULET = new InvItem(235);
	
	private final InvItem[] BEADS = { new InvItem(231), new InvItem(232), new InvItem(233), new InvItem(234) };

	@Override
	public int getQuestId() {
		return Constants.Quests.IMP_CATCHER;
	}

	@Override
	public String getQuestName() {
		return "Imp catcher";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getInventory().add(AMULET);
		player.getActionSender().sendMessage("Well done. You have completed the Imp catcher quest");
		player.incExp(6, 875, false, false, false);
		player.getActionSender().sendMessage("@gre@You have gained 1 quest point!");
		player.incQuestPoints(1);
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if(n.getID() != 117) {
			return;
		}

		p.setBusy(true);
		n.blockedBy(p);

		switch(p.getQuestStage(this)) {
			case 0: // Just starting
				playMessages(p, n, false, "Hello there");

				String[] options = new String[] { "Give me a quest!", "Most of your friends are pretty quiet aren't they?" };

				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch(option) {
							case 0:
								playMessages(owner, n, false, "Give me a quest what?");

								options = new String[] {"Give me a quest please", "Give me a quest or else", "Just stop messing around and give me a quest"};
								owner.setMenuHandler(new MenuHandler(options) {
									@Override
									public void handleReply(int option, String reply) {
										owner.setBusy(true);
										playMessages(owner, n, true, reply);

										switch(option) {
											case 0: // please
												playMessages(owner, n, false, "Well seeing as you asked nicely", "I could do with some help", "The wizard Grayzag next door decided he didn't like me", "So he cast of spell of summoning", "And summoned hundreds of little imps", "These imps stole all sorts of my things", "Most of these things I don't really care about", "They're just eggs and balls of string and things", "But they stole my 4 magical beads", "There was a red one, a yellow one, a black one and a white one", "These imps have now spread out all over the kingdom", "Could you get my beads back for me");
												playMessages(owner, n, true, "I'll try");

												owner.setQuestStage(getQuestId(), 1);

												n.unblock();
												break;
											case 1: // or else
												playMessages(owner, n, false, "Or else what? You'll attack me?", "Hahaha");

												n.unblock();
												break;
											case 2: // messing around
												playMessages(owner, n, false, "Ah now you're just assuming I have on to give");

												n.unblock();
												break;
										}

										owner.setBusy(false);
									}
								});

								owner.getActionSender().sendMenu(options);
								break;
							case 1:
								playMessages(owner, n, false, "Yes they've mostly got their head in the clouds", "Thinking about magic");

								n.unblock();
								break;
						}

						owner.setBusy(false);
					}
				});

				p.getActionSender().sendMenu(options);
				break;
			case 1: // Started quest
				int beadsFound = 0;
				playMessages(p, n, false, "So how are you doing finding my beads?");

				for(InvItem i : BEADS) {
					if(p.getInventory().contains(i)) {
						beadsFound++;
					}
				}

				if(beadsFound <= 3) {
					playMessages(p, n, true, (beadsFound == 0 ? "I've not found any yet" : "I have found some of your beads"));
					playMessages(p, n, false, "Come back when you have them all", "The four colours of beads I need", "Are red,yellow,black and white", "Go chase some imps");
				} else {
					playMessages(p, n, true, "I've got all four beads", "It was hard work I can tell you");
					playMessages(p, n, false, "Give them here and I'll sort out a reward");
					p.getActionSender().sendMessage("You give four coloured beads to Wizard Mizgog");
					playMessages(p, n, false, "Here's you're reward then", "An amulet of accuracy");
					p.getActionSender().sendMessage("The Wizard hands you an amulet");

					for(InvItem i : BEADS) {
						p.getInventory().remove(i);
					}

					p.getActionSender().sendInventory();

					p.setQuestStage(getQuestId(), -1);
					p.sendQuestComplete(getQuestId());
				}

				break;
			case -1: // Done
				options = new String[] { "Got any more quests?", "Most of your friends are pretty quiet aren't they?" };

				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch(option) {
							case 0:
								playMessages(owner, n, false, "No Everything is good with the world today");
								break;
							case 1:
								playMessages(owner, n, false, "Yes they've mostly got their head in the clouds", "Thinking about magic");
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

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 117; // Wizard Mizgog
	}

}

