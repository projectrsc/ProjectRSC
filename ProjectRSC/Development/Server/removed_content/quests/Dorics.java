package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.action.InvUseOnObjectListener;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.world.World;

public final class Dorics extends Quest implements TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.DORICS_QUEST;
	}

	@Override
	public String getQuestName() {
		return "Doric's quest";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getActionSender().sendMessage("You have completed Dorics quest");
		player.incExp(14, 2200, false, false, false);
		player.getActionSender().sendMessage("@gre@You have gained 1 quest point!");
		player.incQuestPoints(1);
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if(n.getID() != 144) {
			return;
		}

		p.setBusy(true);
		n.blockedBy(p);

		switch(p.getQuestStage(this)) {
			case 0: // haven't started
				playMessages(p, n, false, "Hello traveller, what brings you to my humble smithy?");

				String[] options = new String[] { "I wanted to use your anvils", "Mind your own business, shortstuff", "I was just checking out the landscape", "What do you make here?" };

				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch(option) {
							case 0:
								playMessages(owner, n, false, "My anvils get enough work with my own use", "I make amulets, it takes a lot of work.", "If you could get me some more materials I could let you use them");

								options = new String[] { "Yes I will get you materials", "No, hitting rocks is for the boring people, sorry." };

								owner.setMenuHandler(new MenuHandler(options) {
									@Override
									public void handleReply(int option, String reply) {
										owner.setBusy(true);

										switch(option) {
											case 0:
												playMessages(owner, n, true, reply);
												playMessages(owner, n, false, "Well, clay is what I use more than anything. I make casts", "Could you get me 6 clay, and 4 copper ore and 2 iron ore please?", "I could pay a little, and let you use my anvils");
												playMessages(owner, n, true, "Certainly, I will get them for you. goodbye");

												owner.setQuestStage(getQuestId(), 1);

												n.unblock();
												break;
											case 1:
												playMessages(owner, n, true, "No, hitting rocks is for the boring people, sorry");
												playMessages(owner, n, false, "That is your choice, nice to meet you anyway");

												n.unblock();
												break;
										}

										owner.setBusy(false);
									}
								});

								owner.getActionSender().sendMenu(options);
								break;
							case 1:
								playMessages(owner, n, false, "How nice to meet someone with such pleasant manners", "Do come again when you need to shout at someone smaller than you");

								n.unblock();
								break;
							case 2:
								playMessages(owner, n, false, "We have a fine town here, it suits us very well", "Please enjoy your travels. And do visit my friends in their mine");

								n.unblock();
								break;
							case 3:
								playMessages(owner, n, false, "I make amulets. I am the best maker of them in Runescape");
								playMessages(owner, n, true, "Do you have any to sell?");
								playMessages(owner, n, false, "Not at the moment, sorry. Try again later");

								n.unblock();
						}

						owner.setBusy(false);
					}
				});

				p.getActionSender().sendMenu(options);
				break;
			case 1: // started
				playMessages(p, n, false, "Have you got my materials yet traveller?");

				if(p.getInventory().countId(149) >= 6 && p.getInventory().countId(150) >= 4 && p.getInventory().countId(151) >= 2) {
					playMessages(p, n, true, "I have everything you need");
					playMessages(p, n, false, "Many thanks, pass them here please");
					p.getActionSender().sendMessage("You hand the clay, copper and iron to Doric");

					p.getInventory().remove(149, 6);
					p.getInventory().remove(150, 4);
					p.getInventory().remove(151, 151);

					p.getActionSender().sendInventory();

					playMessages(p, n, false, "I can spare you some coins for your trouble");
					p.getActionSender().sendMessage("Doric hands you 180 coins");

					p.getInventory().add(new InvItem(10, 180));
					p.getActionSender().sendInventory();

					playMessages(p, n, false, "Please use my anvils any time you want");

					p.setQuestStage(getQuestId(), -1);
					p.sendQuestComplete(getQuestId());
				} else {
					playMessages(p, n, true, "Sorry, I don't have them all yet");
					playMessages(p, n, false, "Not to worry, stick at it", "Remember I need 6 Clay, 4 Copper ore and 2 Iron ore");
				}

				n.unblock();
				break;
			case -1: // done
				playMessages(p, n, false, "Hello traveller, how is your Metalworking coming along?");
				playMessages(p, n, true, "Not too bad thanks Doric");
				playMessages(p, n, false, "Good, the love of metal is a thing close to my heart");

				n.unblock();
				break;
		}

		p.setBusy(false);
	}

	@Override
	public void onInvUseOnObject(GameObject obj, InvItem item, Player player) {
		player.setBusy(true);

		Npc doric = World.getWorld().getNpc(144, 323, 327, 487, 492, true);
		player.informGroupOfChatMessage(new ChatMessage(doric, "Heh who said you could use that?", player));

		player.setBusy(false);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 144; // Doric
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, InvItem item, Player player) {
		return obj.getID() == 177; // Doric's anvil
	}
}
