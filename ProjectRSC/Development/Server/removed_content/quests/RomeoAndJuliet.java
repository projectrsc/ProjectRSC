package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class RomeoAndJuliet extends Quest implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.ROMEO_N_JULIET;
	}

	@Override
	public String getQuestName() {
		return "Romeo & Juliet";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getActionSender().sendMessage("You have completed the quest of Romeo and Juliet");
		player.getActionSender().sendMessage("@gre@You have gained 5 quest points!");
		player.incQuestPoints(5);
	}

	@Override
	public boolean blockTalkToNpc(Player p, final Npc n) {
		switch (n.getID()) {
		case 30:
		case 31:
		case 32:
		case 33:
			return true;
		}
		return false;
	}

	private void romeoCouldYouFindHer(Player p, final Npc n) {
		playMessages(p, n, false, "could you find her for me?", "please tell her i long to be with her");

		String[] options = new String[] { "Yes, I will tell her how you feel", "I can't, it sounds like work to me" };
		p.setMenuHandler(new MenuHandler(options) {

			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, n, true, reply);

				switch (option) {
				case 0:
					romeoIWillTellHer(owner, n);
					break;
				case 1:
					playMessages(owner, n, false, "well, i guess you are not the romantic type", "goodbye");
					break;
				}
				owner.setBusy(false);
				n.unblock();
			}
		});
		p.getActionSender().sendMenu(options);
	}

	private void romeoIWillTellHer(Player p, Npc n) {
		playMessages(p, n, false, "you are the savior of my heart, thank you.");
		playMessages(p, n, true, "err, yes. ok. that's.....nice.");
		p.setQuestStage(getQuestId(), 1);
	}

	private void handleApathecary(Player p, final Npc n) {
		playMessages(p, n, false, "I am the apothecary", "I have potions to brew. Do you need anything specific?");

		String[] options = new String[] { "Can you make a strength potion?", "Do you know a good potion to make hair fall out?", "Have you got any good potions to give away?" };
		p.setMenuHandler(new MenuHandler(options) {
			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, n, true, reply);

				switch (option) {
				case 0:
					playMessages(owner, n, false, "Yes. But the ingredients are a little hard to find", "If you ever get them I will make it for you. For a cost");

					if (owner.getInventory().countId(219) < 1 || owner.getInventory().countId(220) < 1 || owner.getInventory().countId(10) < 5) {  
						playMessages(owner, n, true, "So what are the ingredients?");
						playMessages(owner, n, false, "You'll need to find the eggs of the deadly red spider", "And a limpwurt root", "Oh and you'll have to pay me 5 coins");
						playMessages(owner, n, true, "Ok, I'll look out for them");
						n.unblock();
					} else {
						playMessages(owner, n, true, "I have the root and spider eggs needed to make it");
						playMessages(owner, n, false, "Well give me them and 5 gold and I'll make you your potion");

						String[] options = new String[] { "Yes ok", "No thanks" };
						owner.setMenuHandler(new MenuHandler(options) {
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);

								switch (option) {
								case 0:
									owner.getActionSender().sendMessage("You give a limpwurt root, some red spiders eggs, and 5 coins to the apothecary");
									sleep(1500);
									owner.getActionSender().sendMessage("The apothecary brews up a potion");
									sleep(1500);
									owner.getActionSender().sendMessage("The apothecary gives you a strength potion");
									sleep(1500);

									owner.getInventory().remove(new InvItem(10, 5));
									owner.getInventory().remove(new InvItem(219, 1));
									owner.getInventory().remove(new InvItem(220, 1));
									owner.getInventory().add(new InvItem(221, 1));
									owner.getActionSender().sendInventory();
									break;
								}
								owner.setBusy(false);
								n.unblock();
							}
						});
					}
					break;
				case 1:
					playMessages(owner, n, false, "I do indeed. I gave it to my mother. That's why I now live alone");
					n.unblock();
					break;
				case 2:
					if(owner.getInventory().countId(58) > 0) {
						playMessages(owner, n, false, "Only that spot cream. Hope you enjoy it");
					} else {
						playMessages(owner, n, false, "Yes, ok. Try this potion");

						owner.getInventory().add(new InvItem(58, 1));
						owner.getActionSender().sendInventory();
					}
					n.unblock();
					break;
				}
				owner.setBusy(false);
			}
		});
		p.getActionSender().sendMenu(options);
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		switch (n.getID()) {
		case 30:
			p.setBusy(true);
			n.blockedBy(p);

			switch (p.getQuestStage(this)) {
			case 0:
				playMessages(p, n, false, "juliet, juliet, juliet! wherefore art thou?", "kind friend, have you seen juliet?", "her and her father seem to have disappeared");

				String[] options = new String[] { "Yes, I have seen her", "No, but that's girls for you", "Can I help find her for you?" };
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch (option) {
						case 0:
							playMessages(owner, n, true, "i think it was her. blonde, stressed");
							playMessages(owner, n, false, "yes, that sounds like her", "please tell her i long to be with her");

							String[] options = new String[] { "Yes, I will tell her", "Sorry, I am too busy. Maybe later?" };
							owner.setMenuHandler(new MenuHandler(options) {

								@Override
								public void handleReply(int option, String reply) {
									owner.setBusy(true);
									playMessages(owner, n, true, reply);

									switch (option) {
									case 0:
										romeoIWillTellHer(owner, n);
										break;
									case 1:
										playMessages(owner, n, false, "well, if you do find her, i would be most grateful");
										break;
									}
									owner.setBusy(false);
									n.unblock();
								}
							});
							owner.getActionSender().sendMenu(options);
							break;
						case 1:
							playMessages(owner, n, false, "not my dear juliet. she is different");
							romeoCouldYouFindHer(owner, n);
							break;
						case 2:
							playMessages(owner, n, false, "oh would you? that would be wonderful!", "please tell her i long to be with her");
							playMessages(owner, n, true, "yes, i will tell her how you feel");
							romeoIWillTellHer(owner, n);
							n.unblock();
							break;
						}

						owner.setBusy(false);
					}
				});
				p.getActionSender().sendMenu(options);
				break;
			case 1:
			case 2:
				if(p.getInventory().countId(56) > 0) { //has message from juliet
					playMessages(p, n, true, "Romeo, I have a message from Juliet");
					p.getActionSender().sendMessage("You pass Juliet's message to Romeo");
					sleep(1500);
					playMessages(p, n, false, "Tragic news. Her father is opposing our marriage", "If her father sees me, he will kill me", "I dare not go near his lands", "She says Father Lawrence can help us", "Please find him for me. Tell him of our plight");
					p.setQuestStage(getQuestId(), 3);
				} else {
					playMessages(p, n, false, "Please find my Juliet. I am so, so sad");
				}
				n.unblock();
				break;
			case 3:
				playMessages(p, n, false, "Please friend, how goes our quest?", "Father Lawrence must be told. Only he can help");
				n.unblock();
				break;
			case 4:
				playMessages(p, n, false, "Did you find the Father, what did he suggest?");

				options = new String[] { "He sent me to the apothecary", "He seems keen for you to marry Juliet" };
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch (option) {
						case 0:
							playMessages(owner, n, false, "I know him. He lives near the town square", "the small house behind the sloped building", "Good Luck");
							break;
						case 1:
							playMessages(owner, n, false, "I think he wants some peace. He was our messenger", "before you were kind enough to help us");
							break;
						}
						owner.setBusy(false);
						n.unblock();
					}
				});
				p.getActionSender().sendMenu(options);
				break;
			case 5:
				if(p.getInventory().countId(57) > 0) {
					playMessages(p, n, false, "Ah, you have the potion. I was told what to do by the good Father", "Better get it to Juliet. She knows what is happening");
				} else {
					playMessages(p, n, false, "I hope the potion is near ready", "It is the last step for the great plan", "I hope i will be with my dear one soon");                   
				}
				n.unblock();
				break;
			case 6:
				playMessages(p, n, true, "Romeo, It's all set. Juliet has the potion");
				playMessages(p, n, false, "Ah right", "What potion would that be then?");
				playMessages(p, n, true, "The one to get her to the crypt");
				playMessages(p, n, false, "Ah right", "So she is dead then. Ah that's a shame", "Thanks for your help anyway");
				p.setQuestStage(getQuestId(), -1);
				p.sendQuestComplete(getQuestId());
				break;
			}

			p.setBusy(false);
			break;
		case 31:
			p.setBusy(true);
			n.blockedBy(p);

			switch (p.getQuestStage(this)) {
			case -1:
				playMessages(p, n, false, "I sat in that cold crypt for ages waiting for Romeo", "That useless fool never showed up", "And all I got was indigestion. I am done with men like him", "Now go away before I call my father!");
				n.unblock();
				break;
			case 0:
				playMessages(p, n, false, "romeo, romeo wherefore art thou romeo", "Bold adventurer, have you seen Romeo on your travels?", "Skinny guy, a bit wishy washy, head full of poetry");

				String[] options = {"Yes i have met him", "No, i think i would have remembered if I had", "I guess i could find him", "I think you could do better"};
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch (option) {
						case 0:
						case 1:
							playMessages(owner, n, false, "Could you please deliver him a message?");

							String[] options = new String[] { "Certainly, I will do so straight away", "No, I have better things to do" };
							owner.setMenuHandler(new MenuHandler(options) {
								@Override
								public void handleReply(int option, String reply) {
									owner.setBusy(true);
									playMessages(owner, n, true, reply);

									switch(option) {
									case 0:
										playMessages(owner, n, false, "It may be our only hope");
										owner.getActionSender().sendMessage("Juliet hands you a message");
										owner.getInventory().add(new InvItem(56, 1));
										owner.getActionSender().sendInventory();
										owner.setQuestStage(getQuestId(), 1);
										owner.setBusy(false);
										break;
									case 1:
										playMessages(owner, n, false, "I will not keep you from them, goodbye.");
										break;
									}
									owner.setBusy(false);
									n.unblock();
								}
							});
							owner.getActionSender().sendMenu(options);
							break;
						case 2:
							playMessages(owner, n, false, "That is most kind of you", "Could you please deliver a message to him?");
							playMessages(owner, n, true, "Certainly, I will deliver your message straight away");
							playMessages(owner, n, false, "It may be our only hope");

							owner.getActionSender().sendMessage("Juliet hands you a message");
							owner.getInventory().add(new InvItem(56, 1));
							owner.getActionSender().sendInventory();
							owner.setQuestStage(getQuestId(), 1);
							n.unblock();
							break;
						case 3:
							playMessages(owner, n, false, "He has his good points", "He doesn't spend all day on the internet at least");
							n.unblock();
							break;
						}
						owner.setBusy(false);
					}
				});
				p.getActionSender().sendMenu(options);
				break;
			case 1: //coming from romeo
				if (p.getInventory().countId(56) > 0) {
					playMessages(p, n, false, "Please, deliver the message to Romeo with all speed");
				} else {
					playMessages(p, n, false, "How could you lose this most important message?", "Please, take this message to him, and please don't lose it");

					p.getActionSender().sendMessage("Juliet gives you a message");
					p.getInventory().add(new InvItem(56, 1));
					p.getActionSender().sendInventory();
				}
				n.unblock();
				break;
			case 2:
				n.unblock();
				break;
			case 3:
				playMessages(p, n, false, "Ah, it seems that you can deliver a message after all", "My faith in you is restored");
				n.unblock();
				break;
			case 4:
				playMessages(p, n, false, "Did you find the Father, what did he suggest?");
				playMessages(p, n, true, "I found the Father. Now i seek the apothecary");
				playMessages(p, n, false, "I do not know where he lives", "but please, make haste. My father is close");
				n.unblock();
				break;
			case 5:
				if (p.getInventory().countId(57) > 0) {
					playMessages(p, n, true, "I have a potion from Father Lawrence", "It should make you seem dead, and get you away from this place");

					p.getActionSender().sendMessage("You pass the potion to Juliet");
					p.getInventory().remove(new InvItem(57, 1));
					p.getActionSender().sendInventory();
					sleep(1500);

					playMessages(p, n, false, "Wonderful. I just hope Romeo can remember to get me from the Crypt", "Many thanks kind friend", "Please go to Romeo, make sure he understands", "He can be a bit dense sometimes");
					p.setQuestStage(getQuestId(), 6);
				} else {
					playMessages(p, n, true, "I have to get a potion made for you", "Not done that bit yet though. Still trying.");
					playMessages(p, n, false, "Fair luck to you, the end is close");
				}
				n.unblock();
				break;
			case 6:
				playMessages(p, n, false, "Have you seen Romeo? He will reward you for your help", "He is the wealth in this story", "I am just the glamour");
				n.unblock();
				break;
			}

			p.setBusy(false);
			break;
		case 32:
			p.setBusy(true);
			n.blockedBy(p);

			switch (p.getQuestStage(this)) {
			case -1:
				playMessages(p, n, false, "Oh to be a father in the times of whiskey", "I sing and I drink and I wake up in the gutters", "Top of the morning to you", "To err is human, to forgive, quite difficult");
				n.unblock();
				break;
			case 0:
			case 1:
			case 2:
				playMessages(p, n, false, "Hello adventurer, do you seek a quest?");

				String[] options = new String[] { "I am always looking for a quest", "No, I prefer just to kill things", "Can you recommend a good bar?" };
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch (option) {
						case 0:
							playMessages(owner, n, false, "Well, I see poor Romeo wandering around the square", "I think he may need help", "I was helping him and Juliet to meet, but it became impossible", "I am sure he can use some help");
							break;
						case 1:
							playMessages(owner, n, false, "That's a fine career in these lands", "There is more that needs killing every day");
							break;
						case 2:
							playMessages(owner, n, false, "Drinking will be the death of you", "But the Blue Moon in the city is cheap enough", "And providing you buy one drink an hour, they let you stay all night");
							break;
						}
						owner.setBusy(false);
						n.unblock();
					}
				});
				p.getActionSender().sendMenu(options);
				break;
			case 3:
				playMessages(p, n, true, "Romeo sent me. He says you can help");
				playMessages(p, n, false, "Ah Romeo, yes. A fine lad, but a little bit confused");
				playMessages(p, n, true, "Juliet must be rescued from her father's control");
				playMessages(p, n, false, "I know just the thing.  A potion to make her appear dead", "Then Romeo can collect her from the crypt", "Go to the Apothecary, tell him I sent you", "You need some Cadava Potion");

				p.setQuestStage(getQuestId(), 4);
				n.unblock();
				break;
			default:
				playMessages(p, n, false, "Did you find the Apothecary?");

				if (p.getInventory().countId(57) > 0) {
					playMessages(p, n, true, "I have the potion");
					playMessages(p, n, false, "Good work. Get the potion to Juliet", "I will tell Romeo to be ready");
				} else {
					if (p.getInventory().countId(55) > 0) {
						playMessages(p, n, true, "I am on my way back to him with the ingredients");
						playMessages(p, n, false, "Good work. Get the potion to Juliet", "I will tell Romeo to be ready");
					} else {
						playMessages(p, n, true, "Yes, i must find some cavada berries");
						playMessages(p, n, false, "Well, take care. They are the poisonous touch", "You will need gloves");
					}
				}
				n.unblock();
				break;
			}

			p.setBusy(false);
			break;
		case 33:
			p.setBusy(true);
			n.blockedBy(p);

			switch (p.getQuestStage(this)) {
			case 4:
				playMessages(p, n, true, "Apothecary. Father Lawrence sent me", "I need some Cadava potion to help Romeo and Juliet");
				playMessages(p, n, false, "Cadava potion. It's pretty nasty and hard to make.", "Wing of Rat, Tail of Frog, Ear of Snake and Horn of Dog", "I have all that, but i need some cadavaberries", "You will have to find them while i get the rest ready", "Bring them here when you have them. Be careful though, they are nasty.");
				p.setQuestStage(getQuestId(), 5);
				n.unblock();
				break;
			case 5:
				if (p.getInventory().countId(57) == 0) {
					if (p.getInventory().countId(55) > 0) {
						playMessages(p, n, false, "Well done, you have the berries");

						p.getActionSender().sendMessage("You hand over the berries");
						sleep(1500);
						p.getActionSender().sendMessage("Which the apothecary shakes up in a vial of strange liquid");
						sleep(1500);

						playMessages(p, n, false, "Here is what you need");

						p.getActionSender().sendMessage("The apothecary gives you a Cadava potion");
						sleep(1500);

						p.getInventory().remove(new InvItem(55, 1));
						p.getInventory().add(new InvItem(57, 1));
						p.getActionSender().sendInventory();
					} else {
						playMessages(p, n, false, "Keep searching for the berries", "They are needed for the potion");
					}
					n.unblock();
				} else {
					handleApathecary(p, n);
				}
				break;
			default:
				handleApathecary(p, n);
				break;
			}

			p.setBusy(false);
			break;
		}
	}

}
