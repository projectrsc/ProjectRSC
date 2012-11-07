package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.world.World;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.action.PlayerKilledNpcListener;
import org.darkquest.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;

public final class WitchesPotion extends Quest implements TalkToNpcListener, TalkToNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {
	
	private final InvItem[] INGREDIENTS = { new InvItem(270), new InvItem(271), new InvItem(241), new InvItem(134) };

	@Override
	public int getQuestId() {
		return Constants.Quests.WITCHS_POTION;
	}

	@Override
	public String getQuestName() {
		return "Witch's potion";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getActionSender().sendMessage("Well done you have completed with witches potion quest");
		player.incExp(6, 225 + 50 * player.getMaxStat(6), false, false, false);
		player.getActionSender().sendMessage("@gre@You have gained 1 quest point!");
		player.incQuestPoints(1);
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if(n.getID() != 148) {
			return;
		}

		p.setBusy(true);
		n.blockedBy(p);

		switch(p.getQuestStage(this)) {
			case 0: // Not started
				playMessages(p, n, false, "Greetings Traveller", "What could you want with an old woman like me?");

				String[] options = new String[] { "I am in search of a quest", "I've heard that you are a witch" };
				p.setMenuHandler(new MenuHandler(options) {

					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch(option) {
							case 0:
								playMessages(owner, n, false, "Hmm maybe I can think of something for you", "Would you like to become more proficient in the dark arts?");

								options = new String[] { "Yes help me become one with my darker side", "No I have my pinrciples and honour", "What you mean improve my magic?" };

								owner.setMenuHandler(new MenuHandler(options) {
									@Override
									public void handleReply(int option, String reply) {
										owner.setBusy(true);

										switch(option) {
											case 0:
												playMessages(owner, n, true, reply);
												startQuest(owner, n);
												break;
											case 1:
												playMessages(owner, n, true, "No, I have my principles and honour");
												playMessages(owner, n, false, "Suit yourself, but you're missing out");

												n.unblock();
												break;
											case 2:
												playMessages(owner, n, true, reply);
												playMessages(owner, n, false, "Yes imporove your magic", "Do you have no sense of drama?");

												options = new String[] {"Yes I'd like to improve my magic", "No I'm not interested", "Show me the mysteries of the dark arts"};
												owner.setMenuHandler(new MenuHandler(options) {
													@Override
													public void handleReply(int option, String reply) {
														owner.setBusy(true);
														playMessages(owner, n, true, reply);

														switch(option) {
															case 0:
																startQuest(owner, n);
																break;
															case 1:
																playMessages(owner, n, false, "Many aren't to start off with", "But I think you'll be drawn back to this place");

																n.unblock();
																break;
															case 2:
																startQuest(owner, n);
																break;
														}

														owner.setBusy(false);
													}
												});

												owner.getActionSender().sendMenu(options);
												break;
										}

										owner.setBusy(false);
									}
								});

								owner.getActionSender().sendMenu(options);

								break;
							case 1:
								playMessages(owner, n, false, "Yes it does seem to be getting fairly common knowledge", "I fear I may get a visit from the witch hunters of Falador before long");
								n.unblock();
								break;
						}

						owner.setBusy(false);
					}
				});

				p.getActionSender().sendMenu(options);
				break;
			case 1: // Started
				boolean hasIngredients = true;

				playMessages(p, n, false, "Greetings Traveller", "So have you found the things for the potion");

				for(InvItem i : INGREDIENTS) {
					if(!p.getInventory().contains(i)) {
						hasIngredients = false;
					}
				}

				if(hasIngredients) {
					playMessages(p, n, true, "Yes I have everything");
					playMessages(p, n, false, "Excellent, can I have them then?");

					p.getActionSender().sendMessage("You pass the ingredients to Hetty");
					p.getActionSender().sendMessage("Hetty put's all the ingredients in her cauldron");

					for(InvItem i : INGREDIENTS) {
						p.getInventory().remove(i);
					}
					p.getActionSender().sendInventory();

					sleep(2000);
					p.getActionSender().sendMessage("Hetty closes her eyes and begins to chant");
					sleep(2000);
					playMessages(p, n, false, "Ok drink from the cauldron");

					p.setQuestStage(getQuestId(), 2);
				} else {
					playMessages(p, n, true, "No not yet");
					playMessages(p, n, false, "Well remember you need to get", "An eye of newt, a rat's tail,some burnt meat and an onion");
				}

				n.unblock();
				break;
			case 2: // Drink from Cauldron
				playMessages(p, n, false, "Well are you going to drink the potion or not?");

				n.unblock();
				break;
			case -1: // Done
				playMessages(p, n, false, "Greetings Traveller", "How's your magic coming along?");
				playMessages(p, n, true, "I'm practicing and slowly getting better");
				playMessages(p, n, false, "good, good");

				n.unblock();
				break;
		}

		p.setBusy(false);
	}

	// Begin the quest
	private void startQuest(Player owner, Npc n) {
		playMessages(owner, n, false, "Ok I'm going to make a potion to help bring out your drarker self", "So that you can perform acts of dark magic with greater ease", "You will need certain ingredients");
		playMessages(owner, n, true, "What do I need");
		playMessages(owner, n, false, "You need an eye of newt, a rat's tail, an onion and a piece of burnt meat");

		owner.setQuestStage(getQuestId(), 1);

		n.unblock();
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if(obj.getID() == 147 && command.equals("drink from")) {
			player.setBusy(true);

			if(player.getQuestStage(this) != 2) {
				player.informGroupOfChatMessage(new ChatMessage(player, "I'd rather not", null));
				sleep(2000);
				player.informGroupOfChatMessage(new ChatMessage(player, "It doesn't look very tasty", null));
				sleep(2000);
			} else {
				player.getActionSender().sendMessage("You drink from the cauldron");
				sleep(2000);
				player.getActionSender().sendMessage("You feel yourself imbued with power");
				sleep(2000);
				player.setQuestStage(getQuestId(), -1);
				player.sendQuestComplete(getQuestId());
			}

			player.setBusy(false);
		}
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() != 29) {
			return;
		}

		// Drop bones and tail
		World.getWorld().registerItem(new Item(271, n.getX(), n.getY(), 1, p));
		World.getWorld().registerItem(new Item(20, n.getX(), n.getY(), 1, p));
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 148; // Hetty
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 147; // Cauldron
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		System.out.println(p.getQuestStage(this));

		return n.getID() == 29 && p.getQuestStage(this) == 1;
	}
}

