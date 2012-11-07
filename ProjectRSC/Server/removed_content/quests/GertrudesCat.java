package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.model.Point;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.action.InvUseOnGroundItemListener;
import org.darkquest.gs.plugins.listeners.action.InvUseOnItemListener;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.plugins.listeners.action.PickupListener;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.action.WallObjectActionListener;
import org.darkquest.gs.plugins.listeners.executive.InvUseOnGroundItemExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.PickupExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.WallObjectActionExecutiveListener;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.World;

public final class GertrudesCat extends Quest implements TalkToNpcExecutiveListener, TalkToNpcListener,
														PickupExecutiveListener, PickupListener,
														WallObjectActionExecutiveListener, WallObjectActionListener,
														InvUseOnItemExecutiveListener, InvUseOnItemListener,
														ObjectActionExecutiveListener, ObjectActionListener,
														InvUseOnGroundItemExecutiveListener, InvUseOnGroundItemListener {

	private static final Point[] CRATE_LOCATIONS = {
		Point.location(67, 440), Point.location(68, 440), Point.location(68, 439),
		Point.location(63, 436), Point.location(62, 436), Point.location(58, 438),
		Point.location(58, 443), Point.location(59, 443), Point.location(59, 442),
		Point.location(60, 446), Point.location(60, 445), Point.location(59, 445),
		Point.location(64, 445), Point.location(65, 445), Point.location(64, 442),
		Point.location(59, 438), Point.location(62, 438)
	};

	@Override
	public int getQuestId() {
		return Constants.Quests.GERTRUDES_CAT;
	}

	@Override
	public String getQuestName() {
		return "Gertrude's Cat (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.incExp(7, 355.0D, false, false, false);
		player.getActionSender().sendStat(7);

		player.incQuestPoints(1);
		player.getActionSender().sendMessage("@gre@You have gained 1 quest point!");

		player.getInventory().add(new InvItem(1096));
		player.getInventory().add(new InvItem(332));
		player.getInventory().add(new InvItem(346));
		player.getActionSender().sendInventory();

		sleep(1000);
		player.getActionSender().sendMessage("well done, you have completed gertrudes cat quest");
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		switch (n.getID()) {
		case 714: //gertrude
		case 715: //shilop
		case 781: //wilough
		case 782: //philop
		case 783: //kanel
			return true;
		}
		return false;
	}

	private void gertrude(final Player p, final Npc n) {
		p.setBusy(true);
		n.setBusy(true);

		switch (p.getQuestStage(this)) {
		case 0:
			playMessages(p, n, true, "hello, are you ok?");
			playMessages(p, n, false, "do i look ok?...those kids drive me crazy", "...i'm sorry, it's just, ive lost her");
			playMessages(p, n, true, "lost who?");
			playMessages(p, n, false, "fluffs, poor fluffs, she never hurt anyone");
			playMessages(p, n, true, "who's fluffs");
			playMessages(p, n, false, "my beloved feline friend fluffs", "she's been purring by my side for almost a decade", "please, could you go search for her...", "...while i look over the kids?");

			String[] options = new String[] { "well, i suppose i could", "what's in it for me?", "sorry, i'm too busy to play pet rescue" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					p.setBusy(true);
					n.setBusy(true);

					playMessages(p, n, true, reply);

					switch (option) {
					case 0:
						gertrude_isuppose(p, n);
						break;
					case 1:
						gertrude_whatsinit(p, n);
						break;
					case 2:
						gertrude_toobusy(p, n);
						break;
					}

					p.setBusy(false);
					n.setBusy(false);
				}
			});
			p.getActionSender().sendMenu(options);
			break;
		case 1:
			playMessages(p, n, true, "hello gertrude");
			playMessages(p, n, false, "have you seen my poor fluffs?");
			playMessages(p, n, true, "i'm afraid not");
			playMessages(p, n, false, "what about shilop?");
			playMessages(p, n, true, "no sign of him either");
			playMessages(p, n, false, "hmmm...stange, he should be at the market");
			break;
		case 2:
		case 3:
			playMessages(p, n, true, "hello gertrude");
			playMessages(p, n, false, "hello again, did you manage to find shilop?", "i can't keep an eye on him for the life of me");
			playMessages(p, n, true, "he does seem quite a handfull");
			playMessages(p, n, false, "you have no idea!.... did he help at all?");
			playMessages(p, n, true, "i think so, i'm just going to look now");
			playMessages(p, n, false, "thanks again adventurer");
			break;
		case 4:
			playMessages(p, n, true, "hello again");
			playMessages(p, n, false, "hello. how's it going?, any luck?");
			playMessages(p, n, true, "yes. i've found fluffs");
			playMessages(p, n, false, "well well, you are clever, did you bring her back?");
			playMessages(p, n, true, "well, that's the thing. she refuses to leave");
			playMessages(p, n, false, "oh dear, oh dear, maybe she's just hungry", "she loves doogle sardines, but i'm all out", "");
			playMessages(p, n, true, "doogle sardines?");
			playMessages(p, n, false, "yes, raw sardines seasoned with doogle leaves", "unfortunatly i've used all my doogle leaves", "but you may find some in the wood out back");
			break;
		case 5:
		case 6:
			playMessages(p, n, true, "hi");
			playMessages(p, n, false, "hey traveller, did fluffs eat the sardines?");
			playMessages(p, n, true, "yeah, she loved them, but she still won't leave");
			playMessages(p, n, false, "well that is strange, there must be a reason!");
			break;
		case 7:
			playMessages(p, n, true, "hello gertrude", "fluffs ran off with her two kittens");
			playMessages(p, n, false, "you're back, thank you, thank you", "fluffs just came back, i think she was upset...", "...as she couldn't find her kittens");

			p.getActionSender().sendMessage("gertrude gives you a hug");
			sleep(1200);

			playMessages(p, n, false, "if you hadn't found her kittens they'd have died out there");
			playMessages(p, n, true, "that's ok, i like to do my bit");
			playMessages(p, n, false, "i don't know how to thank you", "i have no real material possesion..but i do have kittens", "..i can only really look after one");
			playMessages(p, n, true, "well, if it needs a home");
			playMessages(p, n, false, "i would sell it to my cousin in west ardougne..", "i hear there's a rat epidemic there..but it's too far", "here you go, look after her and thank you again");

			p.getActionSender().sendMessage("gertrude gives you a kitten...");
			sleep(2500);
			p.getActionSender().sendMessage("...and some food");
			sleep(2200);
			p.sendQuestComplete(getQuestId());
			p.setQuestStage(getQuestId(), -1);
			break;
		case -1:
			playMessages(p, n, true, "hello again gertrude");
			playMessages(p, n, false, "well hello adventurer, how are you?");
			playMessages(p, n, true, "pretty good thanks, yourself?");
			playMessages(p, n, false, "same old, running after shilob most of the time");
			playMessages(p, n, true, "nevermind, i'm sure he'll calm down with age");
			break;
		}

		p.setBusy(false);
		n.setBusy(false);
	}

	private void gertrude_isuppose(Player p, Npc n) {
		playMessages(p, n, false, "really?, thank you so much", "i really have no idea where she could be", "i think my sons, shilop and wilough, saw the cat last", "they'll be out in the market place");
		playMessages(p, n, true, "alright then, i'll see what i can do");
		p.setQuestStage(getQuestId(), 1);
	}

	private void gertrude_whatsinit(final Player p, final Npc n) {
		playMessages(p, n, false, "i'm sorry. i'm too poor to pay you anything", "the best i could offer is a warm meal", "so, can you help?");

		String[] options = new String[] { "well, i suppose i could", "sorry, i'm too busy to play pet rescue" };
		p.setMenuHandler(new MenuHandler(options) {
			@Override
			public void handleReply(int option, String reply) {
				p.setBusy(true);
				n.setBusy(true);

				playMessages(p, n, true, reply);

				switch (option) {
				case 0:
					gertrude_isuppose(p, n);
					break;
				case 1:
					gertrude_toobusy(p, n);
					break;
				}

				p.setBusy(false);
				n.setBusy(false);
			}
		});
		p.getActionSender().sendMenu(options);
	}

	private void gertrude_toobusy(Player p, Npc n) {
		playMessages(p, n, false, "well, ok then, i'll have to find someone else");
	}


	private void wiloughshilop(final Player p, final Npc n) {
		p.setBusy(true);
		n.setBusy(true);

		switch (p.getQuestStage(this)) {
		case 0:
			playMessages(p, n, true, "hello youngster");	
			playMessages(p, n, false, "i don't talk to strange old people");
			break;
		case 1:
			playMessages(p, n, true, "hello there, i've been looking for you");
			playMessages(p, n, false, "i didn't mean to take it!, i just forgot to pay");
			playMessages(p, n, true, "what?...i'm trying to help your mum find fluffs");
			playMessages(p, n, false, "oh..., well, in that case i might be able to help", "fluffs followed me to my secret play area...", "i haven't seen him since");
			playMessages(p, n, true, "and where is this play area?");
			playMessages(p, n, false, "if i told you that, it wouldn't be a secret");

			String[] options = new String[] { "tell me sonny, or i will hurt you", "what will make you tell me?", "well never mind, fluffs' lost" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					p.setBusy(true);
					n.setBusy(true);

					playMessages(p, n, true, reply);

					switch (option) {
					case 0:
						playMessages(p, n, false, "w..w..what? y..you wouldn't, a young lad like me", "i'd have you behind bars before nightfall");
						p.getActionSender().sendMessage("you decide it's best not to hurt the boy");
						sleep(1000);
						break;
					case 1:
						playMessages(p, n, false, "well...now you ask, i am a bit short on cash");
						playMessages(p, n, true, "how much?");
						playMessages(p, n, false, "100 coins should cover it");
						playMessages(p, n, true, "100 coins!, why should i pay you?");
						playMessages(p, n, false, "you shouldn't, but i won't help otherwise", "i never liked that cat any way, so what do you say?");

						String[] options = new String[] { "i'm not paying you a penny", "ok then, i'll pay" };
						p.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								p.setBusy(true);
								n.setBusy(true);

								playMessages(p, n, true, reply);

								switch (option) {
								case 0:
									playMessages(p, n, false, "ok then, i find another way to make money");
									break;
								case 1:
									if (p.getInventory().contains(new InvItem(10, 100))) {
										playMessages(p, n, true, "there you go, now where did you see fluffs?");
										playMessages(p, n, false, "i play at an abondoned lumber mill to the north...", "just beyond the jolly boar inn...", "i saw fluffs running around in there");
										playMessages(p, n, true, "anything else?");
										playMessages(p, n, false, "well, you'll have to find a broken fence to get in", "i'm sure you can manage that");

										p.getActionSender().sendMessage("you give the lad 100 coins");
										p.getInventory().remove(10, 100);
										p.getActionSender().sendInventory();
										p.setQuestStage(getQuestId(), 2);
									} else {
										playMessages(p, n, true, "but i'll have to get some money first");
										playMessages(p, n, false, "i'll be waiting");
									}
									break;
								}

								p.setBusy(false);
								n.setBusy(false);
							}
						});
						p.getActionSender().sendMenu(options);
						break;
					case 2:
						playMessages(p, n, false, "i'm sure my mum will get over it");
						break;
					}

					p.setBusy(false);
					n.setBusy(false);
				}
			});
			p.getActionSender().sendMenu(options);
			break;

		case 2:
		case 3:
			playMessages(p, n, true, "where did you say you saw fluffs?");
			playMessages(p, n, false, "weren't you listening?, i saw the flee bag...", "...in the old lumber mill just north east of here", "just walk past the jolly boar inn and you should find it");
			break;
		case -1:
		case 4:
		case 5:
		case 6:
			playMessages(p, n, true, "hello again");
			playMessages(p, n, false, "you think you're tough do you?");
			playMessages(p, n, true, "pardon?");
			playMessages(p, n, false, "i can beat anyone up");
			playMessages(p, n, true, "really");
			p.getActionSender().sendMessage("the boy begins to jump around with his fists up");
			sleep(2200);
			p.getActionSender().sendMessage("you decide it's best not to kill him just yet");
			break;
		}

		p.setBusy(false);
		n.setBusy(false);
	}

	@Override
	public boolean blockPickup(Player p, Item i) {
		if (i.getID() == 1093) { //fluffs
			return true;
		}
		return false;
	}

	@Override
	public boolean blockInvUseOnGroundItem(InvItem myItem, Item item, Player player) {
		if (item.getID() == 1093) { //fluffs
			return true;
		}
		return false;
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		if (obj.getID() == 199) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 1039 || obj.getID() == 1040) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockInvUseOnItem(Player player, InvItem item1, InvItem item2) {
		if ((item1.getID() == 354 && item2.getID() == 1100) || (item1.getID() == 1100 && item2.getID() == 354)) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		switch (n.getID()) {
		case 714: //gertrude
			gertrude(p, n);
			break;
		case 715: //shilop
		case 781: //wilough
			wiloughshilop(p, n);
			break;
		case 782: //philop
		case 783: //kanel
			p.getActionSender().sendMessage("The boy's busy playing");
			break;
		}
	}

	@Override
	public void onPickup(Player p, Item i) {
		if (i.getID() == 1093) {
			p.setBusy(true);
			p.getActionSender().sendMessage("you attempt to pick up the cat");
			sleep(1250);

			int stage = p.getQuestStage(this);
			switch (stage) {
			case 7:
				p.getActionSender().sendMessage("the cat is too busy with her kittens to pick up");
				p.setBusy(false);
				break;
			default:
				p.getActionSender().sendMessage("but the cat scratches you");
				p.informGroupOfChatMessage(new ChatMessage(p, "Ouch", null));

				p.setLastDamage(1);
				int newHp = p.getHits() - 1;
				p.setHits(newHp);
				p.informGroupOfModifiedHits(p);
				p.getActionSender().sendStat(3);

				if (newHp <= 0) {
					p.setBusy(false);
					p.killedByNothing();
					return;
				}

				if (stage == 2 || stage == 3) {
					sleep(1000);
					p.getActionSender().sendMessage("the cat seems to be thirsty");

					if (stage == 2) {
						p.setQuestStage(getQuestId(), 3);
					}
				} else if (stage == 4) {
					sleep(1000);
					p.getActionSender().sendMessage("the cat seems to be hungry");
				} else if (stage == 5) {
					sleep(1000);
					p.getActionSender().sendMessage("the cat seems to be afraid");
				}
				p.setBusy(false);
				break;
			}
		}
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player player) {
		if (obj.getID() == 199) {
			player.setBusy(true);
			
			player.getActionSender().sendMessage("you find a crack in the fence");
			player.getActionSender().sendMessage("you walk through");

			switch (player.getX()) {
			case 51:
				player.teleport(50, 438, false);
				break;
			case 50:
				player.teleport(51, 438, false);
				break;
			}
			player.setBusy(false);
			return;
		}
	}

	@Override
	public void onInvUseOnItem(Player player, InvItem item1, InvItem item2) {
		if ((item1.getID() == 354 && item2.getID() == 1100) || (item1.getID() == 1100 && item2.getID() == 354)) {
			player.getActionSender().sendMessage("you rub the doogle leaves over the sardine");
			sleep(1000);
			player.getInventory().remove(354, 1);
			player.getInventory().remove(1100, 1);
			player.getInventory().add(new InvItem(1094));
			player.getActionSender().sendInventory();
			return;
		}
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 1039 || obj.getID() == 1040) {
			player.setBusy(true);

			player.getActionSender().sendMessage("you search the crate...");
			sleep(2000);

			if (player.getQuestStage(this) == 5) { 
				if (player.getCache().hasKey("gert_crate_x") && player.getCache().hasKey("gert_crate_y")) {
					int x = player.getCache().getInt("gert_crate_x");
					int y = player.getCache().getInt("gert_crate_y");

					if (obj.getX() == x && obj.getY() == y) {
						player.getInventory().add(new InvItem(1095));
						player.getActionSender().sendInventory();
						player.getActionSender().sendMessage("...and find two kittens");

						player.setQuestStage(getQuestId(), 6);

						player.getCache().remove("gert_crate_x");
						player.getCache().remove("gert_crate_y");
					} else {
						player.getActionSender().sendMessage("...but find nothing...");
						sleep(2000);
						player.getActionSender().sendMessage("...you hear a cats purring close by");
					}
				}
			} else {
				player.getActionSender().sendMessage("...but find nothing");
			}
			player.setBusy(false);
		}
	}

	@Override
	public void onInvUseOnGroundItem(InvItem myItem, Item item, Player player) {
		if (item.getID() == 1093) { //fluffs
			int stage = player.getQuestStage(this);

			switch (myItem.getID()) {
			case 22: //milk
				switch (stage) {
				case 3:
					player.setBusy(true);

					player.getActionSender().sendMessage("you give the cat some milk");
					sleep(2200);
					player.getActionSender().sendMessage("she really enjoys it");
					sleep(2200);

					player.getActionSender().sendMessage("but now she seems hungry");

					player.getInventory().remove(myItem);
					player.getInventory().add(new InvItem(21));
					player.getActionSender().sendInventory();

					player.setQuestStage(getQuestId(), 4);
					player.setBusy(false);
					break;
				default:
					player.getActionSender().sendMessage("the cat doesn't seem to be thirsty");
					break;
				}
				break;
			case 1094: //seasoned sardine
				switch (stage) {
				case 4:
					player.setBusy(true);

					player.getActionSender().sendMessage("you give the cat the sardine");
					sleep(2200);
					player.getActionSender().sendMessage("the cat gobbles it up");
					sleep(2200);

					player.getActionSender().sendMessage("she still seems scared of leaving");

					player.getInventory().remove(myItem);
					player.getActionSender().sendInventory();

					int random = DataConversions.random(0, CRATE_LOCATIONS.length - 1);

					if (player.getCache().hasKey("gert_crate_x")) {
						player.getCache().update("gert_crate_x", CRATE_LOCATIONS[random].getX());
						player.getCache().update("gert_crate_y", CRATE_LOCATIONS[random].getY());
					} else {
						player.getCache().store("gert_crate_x", CRATE_LOCATIONS[random].getX());
						player.getCache().store("gert_crate_y", CRATE_LOCATIONS[random].getY());
					}

					player.setQuestStage(getQuestId(), 5);
					player.setBusy(false);
					break;
				default:
					player.getActionSender().sendMessage("the cat doesn't seem to be hungry");
					break;
				}
				break;
			case 1095:
				switch (stage) {
				case 6:
					player.setBusy(true);

					player.getActionSender().sendMessage("you place the kittens by their mother");
					sleep(2200);
					player.getActionSender().sendMessage("she purrs at you appreciatively");
					sleep(2200);
					player.getActionSender().sendMessage("and then runs back home with her kittens");
					sleep(1000);

					World.getWorld().unregisterItem(item);
					player.getInventory().remove(myItem);
					player.getActionSender().sendInventory();

					player.setQuestStage(getQuestId(), 7);
					player.setBusy(false);
					break;
				default:
					player.setSuspicious(true);
					break;
				}
				break;
			}
		}
	}

}
