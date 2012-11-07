
package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class SheepShearer extends Quest implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.SHEEP_SHEARER;
	}

	@Override
	public String getQuestName() {
		return "Sheep shearer";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getActionSender().sendMessage("The farmer hands you some coins");
		player.getInventory().add(new InvItem(10, 60));
		player.getActionSender().sendInventory();

		player.getActionSender().sendMessage("Well done you have completed the sheep shearer quest");

		player.incExp(12, 150, false, false, false);

		player.incQuestPoints(1);
		player.getActionSender().sendMessage("@gre@You have gained 1 quest point!");

	}

	@Override
	public boolean blockTalkToNpc(Player p, final Npc n) {
		if(n.getID() == 77) {			
			return true;
		}
		return false;
	}

	private void farmerfred_yes_to_quest(Player p, Npc n) {
		playMessages(p, n, false, "ok i'll see you when you have some wool");
		p.setQuestStage(this, 1);
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if(n.getID() == 77) {			
			int stage = p.getQuestStage(this);
			p.setBusy(true);
			n.blockedBy(p);

			switch (stage) {
			case 0:
				playMessages(p, n, false, "what are you doing on my land?", "you're not the one who keeps leaving all my gates open?", "and letting out all my sheep?");

				String[] options = new String[] { "I'm looking for a quest", "I'm looking for something to kill", "I'm lost" };
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);

						playMessages(owner, n, true, reply);

						switch (option) {
						case 0:
							playMessages(owner, n, false, "you're after a quest, you say?", "actually i could do with a bit of help", "my sheep are getting mighty woolly", "if you could sheer them", "and while your at it spin the wool for me too", "yes, that's it. bring me 20 balls of wool", "and i'm sure i can sort out some sort of payment", "of course, there's the small matter of the thing");

							String[] options = new String[] { "Yes okay. I can do that", "That doesn't sound a very exciting quest", "What do you mean, the thing?" };
							owner.setMenuHandler(new MenuHandler(options) {
								@Override
								public void handleReply(int option, String reply) {
									owner.setBusy(true);

									playMessages(owner, n, true, reply);

									switch (option) {
									case 0:
										farmerfred_yes_to_quest(owner, n);
										break;
									case 1:
										playMessages(owner, n, false, "well what do you expect if you ask a farmer for a quest?", "now are you going to help me or not?");

										String[] options = new String[] { "Yes okay. I can do that", "No I'll give it a miss" };
										owner.setMenuHandler(new MenuHandler(options) {
											@Override
											public void handleReply(int option, String reply) {
												owner.setBusy(true);

												playMessages(owner, n, true, reply);

												switch (option) {
												case 0:
													farmerfred_yes_to_quest(owner, n);
													break;
												}
												owner.setBusy(false);
												n.unblock();
											}
										});
										owner.getActionSender().sendMenu(options);
										owner.setBusy(false);

										break;
									case 2:
										playMessages(owner, n, false, "i wouldn't worry about it", "something ate all the previous shears", "they probably got unlucky", "so are you going to help me?");

										options = new String[] { "Yes okay. I can do that", "Erm I'm a bit worried about this thing" };
										owner.setMenuHandler(new MenuHandler(options) {
											@Override
											public void handleReply(int option, String reply) {
												owner.setBusy(true);

												playMessages(owner, n, true, reply);

												switch (option) {
												case 0:
													farmerfred_yes_to_quest(owner, n);
													break;
												case 1:
													playMessages(owner, n, false, "i'm sure it's nothing to worry about", "it's possible the other shearers aren't dead at all", "and are just hiding in the woods or something");
													playMessages(owner, n, true, "i'm not convinced");
													break;
												}
												owner.setBusy(false);
												n.unblock();
											}
										});
										owner.getActionSender().sendMenu(options);
										owner.setBusy(false);
										break;
									}

									owner.setBusy(false);
									n.unblock();
								}
							});
							owner.getActionSender().sendMenu(options);
							owner.setBusy(false);
							break;
						case 1:
							playMessages(owner, n, false, "what on my land?", "leave my livestock alone you scoundrel");
							owner.setBusy(false);
							n.unblock();
							break;
						case 2:
							playMessages(owner, n, false, "how can you be lost?", "just follow the road east and south", "you'll end up in Lumbridge fairly quickly");
							owner.setBusy(false);
							n.unblock();
							break;
						}
					}
				});
				p.getActionSender().sendMenu(options);
				p.setBusy(false);
				break;
			case 1:
				playMessages(p, n, false, "how are you doing getting those balls of wool?");

				int count = p.getInventory().countId(207);
				if (count > 0) {
					playMessages(p, n, true, "i have some");
					playMessages(p, n, false, "give em here then");

					while (p.getInventory().countId(207) > 0) {
						p.setBusy(true);

						p.getActionSender().sendMessage("You give Fred a ball of wool");

						p.getInventory().remove(207, 1);
						p.getActionSender().sendInventory();

						if (p.getCache().hasKey("wool_ball_cnt")) {
							int cnt = p.getCache().getInt("wool_ball_cnt");
							p.getCache().update("wool_ball_cnt", cnt + 1);

							if (cnt >= 19) {
								break;
							}
						} else {
							p.getCache().store("wool_ball_cnt", 1);
						}

						sleep(2500);
					}

					if (p.getCache().hasKey("wool_ball_cnt")) {
						int cnt = p.getCache().getInt("wool_ball_cnt");

						if (cnt >= 20) {
							p.getCache().remove("wool_ball_cnt");
							playMessages(p, n, true, "that's all of them");
							playMessages(p, n, false, "i guess i'd better pay you then");
							p.sendQuestComplete(getQuestId());
							p.setQuestStage(getQuestId(), -1);
						} else {
							playMessages(p, n, true, "that's all i've got so far");
							playMessages(p, n, false, "i need more before i can pay you");
						}
					}
					p.setBusy(false);
					n.unblock();
				} else {
					if (p.getInventory().countId(145) > 1) {
						playMessages(p, n, true, "well i've got some wool", "i've not managed to make it into a ball though");
						playMessages(p, n, false, "well go find a spinning wheel then", "and get spinning");
					} else {
						playMessages(p, n, true, "i haven't got any at the moment");
						playMessages(p, n, false, "ah well at least you haven't been eaten");
					}

					p.setBusy(false);
					n.unblock();
				}
				break;
			case -1:
				playMessages(p, n, false, "what are you doing on my land?", "you're not the one who keeps leaving all my gates open?", "and letting out all my sheep?");

				options = new String[] { "I'm looking for something to kill", "I'm lost" };
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						n.setBusy(true);

						playMessages(owner, n, true, reply);

						switch (option) {
						case 0:
							playMessages(owner, n, false, "what on my land?", "leave my livestock alone you scoundrel");
							break;
						case 1:
							playMessages(owner, n, false, "how can you be lost?", "just follow the road east and south", "you'll end up in Lumbridge fairly quickly");
							break;
						}

						owner.setBusy(false);
						n.unblock();
					}
				});
				p.getActionSender().sendMenu(options);
				p.setBusy(false);
				break;
			}
		}
	}

}
