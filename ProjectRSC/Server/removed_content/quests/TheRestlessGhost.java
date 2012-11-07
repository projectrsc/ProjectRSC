package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.event.impl.FightEvent;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.action.InvUseOnObjectListener;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.plugins.listeners.action.PickupListener;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.PickupExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.states.Action;
import org.darkquest.gs.world.World;

public final class TheRestlessGhost extends Quest implements TalkToNpcListener, TalkToNpcExecutiveListener, ObjectActionListener, InvUseOnObjectListener, PickupListener, InvUseOnObjectExecutiveListener, ObjectActionExecutiveListener, PickupExecutiveListener {
	
	private String[] options;

	@Override
	public int getQuestId() {
		return Constants.Quests.THE_RESTLESS_GHOST;
	}

	@Override
	public String getQuestName() {
		return "The restless ghost";
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getActionSender().sendMessage("You have completed the restless ghost quest");
		player.incExp(5, 1125, false, false, false);
		player.getActionSender().sendMessage("@gre@You have gained 1 quest point!");
		player.incQuestPoints(1);
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() != 9 || (n.getID() != 15 && p.getQuestStage(this) == -1) || n.getID() != 10) {
			return;
		}
		n.blockedBy(p);
		p.setBusy(true);

		switch(n.getID()) {
			case 9: // Preist
				switch(p.getQuestStage(this)) {
					case 0: // havent' started
						playMessages(p, n, false, "Welcome to the church of the holy Saradomin");

						options = new String[] { "Who's Saradomin?", "Nice place you've got here", "I'm looking for a quest" };

						p.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);

								switch(option) {
									case 0: // who's saradomin
										priestWhosSaradomin(owner, n);
										break;
									case 1: // nice place
										priestNicePlace(owner, n);
										break;
									case 2: // looking for quest
										playMessages(owner, n, false, "That's lucky, I need someone to do a quest for me");
										playMessages(owner, n, true, "Ok I'll help");
										playMessages(owner, n, false, "Ok the problem is, there is a ghost in the church graveyard", "I would like you to get rid of it", "If you need any help", "My friend father Urhney is an expert on ghosts", "I believe he is currently living as a hermit", "He has a little shack somewhere in the swamps south of here", "I'm sure if you told him that I sent you he'd be willing to help", "My name is father Aereck by the way", "Be careful going through the swamps", "I have heard they can be quite dangerous");

										owner.setQuestStage(getQuestId(), 1);
										n.unblock();
										break;
								}

								owner.setBusy(false);
							}
						});

						p.getActionSender().sendMenu(options);
						break;
					case 1: // just started
						playMessages(p, n, false, "Have you got rid of the ghost yet?");
						playMessages(p, n, true, "I can't find father Urhney at the moment");
						playMessages(p, n, false, "Well to get to the swamp he is in", "you need to go round the back of the castle", "The swamp is on the otherside of the fence to the south", "You'll have to go through the wood to the west to get round the fence", "Then you'll have to go right into the eastern depths of the swamp");

						n.unblock();
						break;
					case 2: // got amulet
						playMessages(p, n, false, "Have you got rid of the ghost yet?");
						playMessages(p, n, true, "I had a talk with father Urhney", "He has given me this funny amulet to talk to the ghost with");
						playMessages(p, n, false, "I always wondered what that amulet was", "Well I hope it's useful. Tell me if you get rid of the ghost");

						n.unblock();
						break;
					case 3: // need skull
						priestNeedSkull(p, n);
						n.unblock();
						break;
					case 4: // we should have skull, but we might not
						if(p.getInventory().contains(new InvItem(27))) {
							playMessages(p, n, false, "Have you got rid of the ghost yet?");
							playMessages(p, n, true, "I've finally found the ghost's skull");
							playMessages(p, n, false, "Great. Put it in the ghost's coffin and see what happens!");

							n.unblock();
						} else {
							priestNeedSkull(p, n);
						}
						break;
					case -1:
						playMessages(p, n, false, "Welcome to the church of the holy Saradomin");

						options = new String[] { "Who's Saradomin?", "Nice place you've got here", "I'm looking for a quest" };

						p.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);

								switch(option) {
									case 0: // who's saradomin
										priestWhosSaradomin(owner, n);
										break;
									case 1: // nice place
										priestNicePlace(owner, n);
										break;
									case 2: // looking for quest
										playMessages(owner, n, false, "Sorry I only had the one quest");

										n.unblock();
										break;
								}

								owner.setBusy(false);
							}
						});

						p.getActionSender().sendMenu(options);
						break;
				}
				break;
			case 10: // Urhney
				playMessages(p, n, false, "Go away I'm meditating");

				switch(p.getQuestStage(this)) {
					case 1: // started
						options = new String[] { "Father Aereck sent me to talk to you", "Well that's friendly", "I've come to reposses your house" };
						p.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);

								switch(option) {
									case 0: // father aereck
										playMessages(owner, n, false, "I suppose I'd better talk to you then", "What problems has he got himself into this time?");

										options = new String[] { "He's got a ghost haunting his graveyard", "You mean he gets himself into lots of problems?" };
										owner.setMenuHandler(new MenuHandler(options) {
											@Override
											public void handleReply(int option, String reply) {
												owner.setBusy(true);
												playMessages(owner, n, true, reply);

												switch(option) {
													case 0: // ghost haunting
														urhneyProblem(owner, n);
														break;
													case 1: // lots of problems
														playMessages(owner, n, false, "Yeah. For example when we were trainee preists", "He kept on getting stuck up bell ropes", "Anyway I don't have time for chitchat", "What's his problem this time?");
														playMessages(owner, n, true, "He's got a ghost haunting his graveyard");

														urhneyProblem(owner, n);
														break;
												}

												owner.setBusy(false);
											}
										});
										owner.getActionSender().sendMenu(options);

										break;
									case 1: // friendly
										urhneyFriendly(owner, n);
										break;
									case 2: // reposess your house
										urhneyHouse(owner, n);
										break;
								}

								owner.setBusy(false);
							}
						});
						p.getActionSender().sendMenu(options);
						break;

					case 2: // got amulet at least once
					case 3: // we still need to be able to talk to ghost
					case 4: // still need to talk to ghost
						if(p.getInventory().contains(new InvItem(24))) { // We have the amulet
							urhneyNormal(p, n);
						} else { // Ask for a new one
							options = new String[] { "I've lost the amulet", "Well that's friendly", "I've come to reposses your house" };

							p.setMenuHandler(new MenuHandler(options) {
								@Override
								public void handleReply(int option, String reply) {
									owner.setBusy(true);

									switch(option) {
										case 0: // new amulet
											owner.getActionSender().sendMessage("Father Urhney sighs");
											sleep(2000);
											playMessages(owner, n, false, "How careless can you get", "Those things aren't easy to come by you know", "It's a good job I've got a spare");
											owner.getActionSender().sendMessage("Father Urhney hands you an amulet");

											owner.getInventory().add(new InvItem(24));
											owner.getActionSender().sendInventory();
											sleep(2000);

											playMessages(owner, n, false, "Be more careful this time");
											playMessages(owner, n, true, "Ok I'll try to be");

											n.unblock();
											break;
										case 1: // friendly
											playMessages(owner, n, true, reply);
											urhneyFriendly(owner, n);
											break;
										case 2: // reposess your house
											playMessages(owner, n, true, reply);
											urhneyHouse(owner, n);
											break;
									}

									owner.setBusy(false);
								}
							});
							p.getActionSender().sendMenu(options);

						}
						break;
					default:
						urhneyNormal(p, n);
						break;
				}
				break;
			case 15: // Ghost
				playMessages(p, n, true, "Hello ghost, how are you?");

				switch(p.getQuestStage(this)) {
					case 0: // haven't started
					case 1: // started
						ghostBabble(p, n);
						break;
					case 2: // we should have amulet now
						if(p.getInventory().contains(new InvItem(24))) { // we have the amulet
							playMessages(p, n, false, "Not very good actually");
							playMessages(p, n, true, "What's the problem then?");
							playMessages(p, n, false, "Did you just understand what I said?");

							options = new String[] { "Yep, now tell me what the problem is", "No you sound like you're speaking nonsense to me", "Wow, this amulet works" };

							p.setMenuHandler(new MenuHandler(options) {
								@Override
								public void handleReply(int option, String reply) {
									owner.setBusy(true);

									switch(option) {
										case 0: // yep
											playMessages(owner, n, true, reply);
											playMessages(owner, n, false, "Wow this is incredible, I didn't expect any one to understand me again");
											playMessages(owner, n, true, "Yes, yes I can understand you");
											playMessages(owner, n, true, "But have you any idea why you're doomed to be a ghost?");
											playMessages(owner, n, false, "I'm not sure");
											whyGhost(owner, n);
											break;
										case 1: // no
											playMessages(owner, n, true, "No");
											playMessages(owner, n, false, "Oh that's a pity. You got my hopes up there");
											playMessages(owner, n, true, "Yeah it is pity. Sorry");
											playMessages(owner, n, false, "Hang on a second. You can understand me");

											options = new String[] { "No I can't", "Yep clever aren't I" };
											owner.setMenuHandler(new MenuHandler(options) {
												@Override
												public void handleReply(int option, String reply) {
													owner.setBusy(true);
													playMessages(owner, n, true, reply);

													switch(option) {
														case 0: // no
															playMessages(owner, n, false, "I don't know, the first person I can speak to in ages is a moron");

															n.unblock();
															break;
														case 1: // yep
															playMessages(owner, n, false, "I'm impressed", "You must be very powerfull", "I don't suppose you can stop me being a ghost?");

															ghostRequestHelp(owner, n);
															break;
													}

													owner.setBusy(false);
												}
											});
											owner.getActionSender().sendMenu(options);
											break;
										case 2: // amulet works
											playMessages(owner, n, true, reply);
											playMessages(owner, n, false, "Oh its your amulet that's doing it. I did wonder", "I don't suppose you can help me? I don't like being a ghost");

											ghostRequestHelp(owner, n);
											break;
									}

									owner.setBusy(false);
								}
							});
							p.getActionSender().sendMenu(options);
						} else { // don't have amulet
							ghostBabble(p, n);
						}
						break;
					case 3: // he's told us where skull is
						ghostDontHaveSkull(p, n);
						break;
					case 4: // should have skull
						if(!p.getInventory().contains(new InvItem(27))) {
							ghostDontHaveSkull(p, n);
						} else {
							playMessages(p, n, false, "How are you doing finding my skull?");
							playMessages(p, n, true, "I have found it");
							playMessages(p, n, false, "Hurrah now I can stop being a ghost", "You just need to put in my coffin over there", "And I will be free");

							n.unblock();
						}
						break;
				}
				break;
		}

		p.setBusy(false);
	}

	private void priestNicePlace(Player p, Npc n) {
		playMessages(p, n, false, "It is, isn't it?", "It was built 230 years ago");

		n.unblock();
	}

	private void priestWhosSaradomin(final Player p, final Npc n) {
		playMessages(p, n, false, "Surely you have heard of the God, Saradomin?", "He who creates the forces of goodness and purity in this world?", "I cannot believe your ignorance!", "This is the God with more followers than any other!", "At least in these parts!", "He who along with his brothers Guthix and Zamorak created this world");

		options = new String[] { "Oh that Saradomin", "Oh sorry I'm not from this world" };
		p.setMenuHandler(new MenuHandler(options) {
			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, n, true, reply);

				switch(option) {
					case 0: // oh that
						playMessages(owner, n, false, "There is only one Saradomin");

						n.unblock();
						break;
					case 1: // not from this world
						playMessages(owner, n, false, "That's strange", "I thought things from this world were all slime and tenticles");

						options = new String[] { "You don't understand. This is a computer game", "I am - do you like my disguise?" };

						owner.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);

								switch(option) {
									case 0: // computer game
										playMessages(owner, n, false, "I beg your pardon?");
										playMessages(owner, n, true, "Never mind");

										n.unblock();
										break;
									case 1: // disguise
										playMessages(owner, n, false, "Aarg begon foul creature from another dimension");
										playMessages(owner, n, true, "Ok, Ok, It was a joke");

										n.unblock();
										break;
								}

								owner.setBusy(false);
							}
						});
						p.getActionSender().sendMenu(options);
						break;
				}

				owner.setBusy(false);
			}
		});
		p.getActionSender().sendMenu(options);

	}

	private void priestNeedSkull(Player p, Npc n) {
		playMessages(p, n, false, "Have you got rid of the ghost yet?");
		playMessages(p, n, true, "I've found out that the ghost's corpse has lost its skull", "If I can find the skull the ghost will go");
		playMessages(p, n, false, "That would explain it", "Well I haven't seen any skulls");
		playMessages(p, n, true, "Yes I think a warlock has stolen it");
		playMessages(p, n, false, "I hate warlocks", "Ah well good luck");

		n.unblock();
	}

	private void urhneyNormal(Player p, final Npc n) {
		options = new String[] { "Well that's friendly", "I've come to reposses your house" };

		p.setMenuHandler(new MenuHandler(options) {
			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, n, true, reply);

				switch(option) {
					case 0: // friendly
						urhneyFriendly(owner, n);
						break;
					case 1: // reposess your house
						urhneyHouse(owner, n);
						break;
				}

				owner.setBusy(false);
			}
		});

		p.getActionSender().sendMenu(options);
	}

	private void urhneyProblem(Player p, Npc n) {
		playMessages(p, n, false, "Oh the silly fool", "I leave town for just five months", "and already he can't manage", "Sigh", "Well I can't go back and exorcise it", "I vowed not to leave this place", "Until I had done a full two years of prayer and meditation", "Tell you what I can do though", "Take this amulet");
		p.getActionSender().sendMessage("Father Urhney hands you an amulet");

		p.getInventory().add(new InvItem(24));
		p.getActionSender().sendInventory();
		sleep(2000);

		playMessages(p, n, false, "It is an amulet of Ghostspeak", "So called because when you wear it you can speak to ghosts", "A lot of ghosts are doomed to be ghosts", "Because they have left some task uncompleted", "Maybe if you know what this task is", "You can get rid of the ghost", "I'm not making any guarantees mind you", "But it is the best I can do right now");
		playMessages(p, n, true, "Thank you. I'll give it a try");

		p.setQuestStage(getQuestId(), 2);

		n.unblock();
	}

	private void urhneyFriendly(Player p, Npc n) {
		playMessages(p, n, false, "I said go away!");
		playMessages(p, n, true, "Ok, ok");

		n.unblock();
	}

	private void urhneyHouse(Player p, final Npc n) {
		playMessages(p, n, false, "Under what grounds?");

		options = new String[] { "Repeated failure on mortgage payments", "I don't know, I just wanted this house" };
		p.setMenuHandler(new MenuHandler(options) {
			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, n, true, reply);

				switch(option) {
					case 0: // mortgage
						playMessages(owner, n, false, "I don't have a mortgage", "I built this house myself");
						playMessages(owner, n, true, "Sorry I must have got the wrong address", "All the houses look the same around here");

						n.unblock();
						break;
					case 1: // wants house
						playMessages(owner, n, false, "Oh go away and stop wasting my time");

						n.unblock();
						break;
				}

				owner.setBusy(false);
			}
		});
		p.getActionSender().sendMenu(options);
	}

	private void ghostBabble(Player p, final Npc n) {
		playMessages(p, n, false, "Wooo wooo wooooo");

		options = new String[] { "Sorry I don't speak ghost", "Ooh that's interesting", "Any hints where I can find some treasure?" };
		p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);

						switch(option) {
							case 0: // don't speak ghost
								dontSpeakGhost(owner, n);
								break;
							case 1: // that's interesting
								playMessages(owner, n, false, "Woo woo", "Woooooooooooooooooo");

								options = new String[] { "Did he really?", "Yeah that's what I thought" };

								owner.setMenuHandler(new MenuHandler(options) {
									@Override
									public void handleReply(int option, String reply) {
										owner.setBusy(true);
										playMessages(owner, n, true, reply);

										switch(option) {
											case 0: // did he really?
												playMessages(owner, n, false, "Woo");

												options = new String[] { "My brother had exactly the same problem", "Goodbye. Thanks for the chat" };

												owner.setMenuHandler(new MenuHandler(options) {
													@Override
													public void handleReply(int option, String reply) {
														owner.setBusy(true);
														playMessages(owner, n, true, reply);

														switch(option) {
															case 0: // brother
																playMessages(owner, n, false, "Woo Wooooo", "Wooooo Woo Woo Woo");

																options = new String[] { "Goodbye. Thanks for the chat", "You'll have to give me the recipe some time" };

																owner.setMenuHandler(new MenuHandler(options) {
																	public void handleReply(int option, String reply) {
																		owner.setBusy(true);
																		playMessages(owner, n, true, reply);

																		switch(option) {
																			case 0: // goodbye
																				goodbyeGhost(owner, n);
																				break;
																			case 1: //recipe
																				playMessages(owner, n, false, "Wooooooo woo woooooooo");

																				options = new String[] { "Goodbye. Thanks for the chat", "Hmm I'm not sure about that" };

																				owner.setMenuHandler(new MenuHandler(options) {
																					public void handleReply(int option, String reply) {
																						owner.setBusy(true);
																						playMessages(owner, n, true, reply);

																						switch(option) {
																							case 0: // goodbye
																								goodbyeGhost(owner, n);
																								break;
																							case 1: // not sure
																								notSureGhost(owner, n);
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
															case 1: // goodbye
																goodbyeGhost(owner, n);
																break;
														}

														owner.setBusy(false);
													}
												});

												owner.getActionSender().sendMenu(options);
												break;
											case 1: // yeah that's what I thought
												playMessages(owner, n, false, "Wooo woooooooooooooo");

												options = new String[] { "Goodbye. Thanks for the chat", "Hmm I'm not sure about that" };

												owner.setMenuHandler(new MenuHandler(options) {
													@Override
													public void handleReply(int option, String reply) {
														owner.setBusy(true);
														playMessages(owner, n, true, reply);

														switch(option) {
															case 0: // goodbye
																goodbyeGhost(owner, n);
																break;
															case 1: // not sure
																notSureGhost(owner, n);
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
							case 2: // treasure
								playMessages(owner, n, false, "Wooooooo woo!");

								options = new String[] { "Sorry I don't speak ghost", "Thank you. You've been very helpful" };

								owner.setMenuHandler(new MenuHandler(options) {
									@Override
									public void handleReply(int option, String reply) {
										owner.setBusy(true);
										playMessages(owner, n, true, reply);

										switch(option) {
											case 0: // don't speak ghost
												dontSpeakGhost(owner, n);
												break;
											case 1: // very helpful
												playMessages(owner, n, false, "Wooooooo");

												n.unblock();
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

		p.getActionSender().sendMenu(options);

	}

	private void dontSpeakGhost(Player p, Npc n) {
		playMessages(p, n, false, "Woo woo?");
		playMessages(p, n, true, "Nope still don't understand you");
		playMessages(p, n, false, "Woooooooo");
		playMessages(p, n, true, "Never mind");

		n.unblock();
	}

	private void goodbyeGhost(Player p, Npc n) {
		playMessages(p, n, false, "Wooo wooo");

		n.unblock();
	}

	private void notSureGhost(Player p, Npc n) {
		playMessages(p, n, false, "Wooo woo");
		playMessages(p, n, true, "Well if you insist");
		playMessages(p, n, false, "Wooooooooo");
		playMessages(p, n, true, "Ah well, better be off now");
		playMessages(p, n, false, "Woo");
		playMessages(p, n, true, "Bye");

		n.unblock();
	}

	private void ghostRequestHelp(Player p, final Npc n) {
		options = new String[] { "Yes, Ok. Do you know why you're a ghost?", "No, you're scary" };
		p.setMenuHandler(new MenuHandler(options) {
			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, n, true, reply);

				switch(option) {
					case 0: // why you're a ghost
						playMessages(owner, n, false, "No, I just know I can't do anything much like this");
						whyGhost(owner, n);
						break;
					case 1: // you're scary
						n.unblock();
						break;
				}

				owner.setBusy(false);
			}
		});
		p.getActionSender().sendMenu(options);
	}

	private void whyGhost(Player p, Npc n) {
		playMessages(p, n, true, "I've been told a certain task may need to be completed", "So you can rest in peace");
		playMessages(p, n, false, "I should think it is probably because", "A warlock has come and stolen my skull", "If you look inside my coffin there", "you'll find my corpse without a head on it");
		playMessages(p, n, true, "Do you know where this warlock might be now?");
		playMessages(p, n, false, "I think it was one of the warlocks who lives in the big tower", "In the sea southwest from here");
		playMessages(p, n, true, "Ok I will try and get the skull back for you, so you can rest in peace");
		playMessages(p, n, false, "Ooh thank you. That would be such a great relief");

		p.setQuestStage(getQuestId(), 3);

		n.unblock();
	}

	private void ghostDontHaveSkull(Player p, Npc n) {
		if(p.getInventory().contains(new InvItem(24))) { // we have the amulet
			playMessages(p, n, false, "How are you doing finding my skull?");

			playMessages(p, n, true, "Sorry, I can't find it at the moment");
			playMessages(p, n, false, "Ah well keep on looking", "I'm pretty sure it's somewhere in the tower south west from here", "There's a lot of levels to the tower, though", "I supposed it might take a little while to find");

			n.unblock();
		} else { // we don't have the amulet
			ghostBabble(p, n);
		}
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (command.equals("search") && obj.getID() == 40) { // search the coffin
			player.getActionSender().sendMessage((player.getQuestStage(this) == -1 ? "There is a nice and complete skeleton in here" : "There's a skeleton without a skull in here"));
		}
	}

	@Override
	public void onInvUseOnObject(GameObject obj, InvItem item, Player player) {
		if(obj.getID() == 40 && player.getQuestStage(this) == 4 && item.getID() == 27) {
			player.setBusy(true);
			player.getActionSender().sendMessage("You put the skull in the coffin");
			player.getInventory().remove(27, 1);
			player.getActionSender().sendInventory();
			sleep(2000);
			player.getActionSender().sendMessage("The ghost has vanished");
			// TODO actually make the ghost vanish
			player.setQuestStage(getQuestId(), -1);
			sleep(2000);
			player.getActionSender().sendMessage("You think you hear a faint voice in the air");
			sleep(2000);
			player.getActionSender().sendMessage("Thank you");
			sleep(2000);
			player.sendQuestComplete(getQuestId());
			player.setBusy(false);
		}
	}

	@Override
	public void onPickup(Player p, Item i) {
		switch(p.getQuestStage(this)) {
			case 3: // We haven't attempted to pick it up yet
				p.getInventory().add(new InvItem(27));
				p.getActionSender().sendInventory();

				i.remove();

				p.setQuestStage(getQuestId(), 4);

				Npc skeleton = new Npc(50, 218, 3521, 218, 218, 3521, 3521);
				p.getActionSender().sendMessage("Out of nowhere a skeleton appears");

				World.getWorld().registerNpc(skeleton);
				skeleton.setShouldRespawn(false);

				fight(p, skeleton);

				// TODO remove skeleton
				break;
			case 4: // We already "killed" the skeleton, but we probably lost the skull
				p.getInventory().add(new InvItem(27));
				p.getActionSender().sendInventory();

				i.remove();
				break;
			default:
				p.setBusy(true);
				p.informGroupOfChatMessage(new ChatMessage(p, "That skull is scary", null));
				sleep(2000);
				p.informGroupOfChatMessage(new ChatMessage(p, "I've got no reason to take it", null));
				sleep(2000);
				p.informGroupOfChatMessage(new ChatMessage(p, "I think I'll leave it alone", null));
				sleep(2000);
				p.setBusy(false);
				break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 9 || (n.getID() == 15 && p.getQuestStage(this) != -1) || n.getID() == 10;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, InvItem item, Player player) {
		return item.getID() == 27 && obj.getID() == 40;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 40;
	}

	@Override
	public boolean blockPickup(Player p, Item i) {
		return i.getID() == 27 && i.isOn(218, 3521) && i.getOwner() == null; // The skull located at (218, 3521) that hasn't been dropped by anyone
	}

	void fight(Player owner, Npc n) {
		n.resetPath();
		owner.resetPath();
		owner.resetAll();
		owner.setStatus(Action.FIGHTING_MOB);
		owner.getActionSender().sendSound("underattack");
		owner.getActionSender().sendMessage("You are under attack!");

		n.setLocation(owner.getLocation(), true);
		
		for (Player p : n.getViewArea().getPlayersInView()) {
			p.removeWatchedNpc(n);
		}

		owner.setBusy(true);
		owner.setSprite(9);
		owner.setOpponent(n);
		owner.setCombatTimer();

		n.setBusy(true);
		n.setSprite(8);
		n.setOpponent(owner);
		n.setCombatTimer();
		FightEvent fighting = new FightEvent(owner, n, true);
		fighting.setLastRun(0);
		World.getWorld().getDelayedEventHandler().add(fighting);
	}
}
