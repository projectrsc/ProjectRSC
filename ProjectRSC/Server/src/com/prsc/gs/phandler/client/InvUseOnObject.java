package com.prsc.gs.phandler.client;

import java.util.List;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.config.Formulae;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.event.MiniEvent;
import com.prsc.gs.event.ShortEvent;
import com.prsc.gs.event.impl.WalkToObjectEvent;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.external.ItemSmithingDef;
import com.prsc.gs.external.ItemWieldableDef;
import com.prsc.gs.model.Bubble;
import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.states.Action;
import com.prsc.gs.tools.DataConversions;
import com.prsc.gs.util.Logger;
import com.prsc.gs.world.ActiveTile;
import com.prsc.gs.world.World;


public class InvUseOnObject implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private void handleDoor(final Player player, final ActiveTile tile, final GameObject object, final int dir, final InvItem item) {
        player.setStatus(Action.USING_INVITEM_ON_DOOR);
        World.getWorld().getDelayedEventHandler().add(new WalkToObjectEvent(player, object, false) {
            public void arrived() {
                owner.resetPath();
                if (owner.isBusy() || owner.isRanging() || !owner.getInventory().contains(item) || !tile.hasGameObject() || !tile.getGameObject().equals(object) || owner.getStatus() != Action.USING_INVITEM_ON_DOOR) {
                    return;
                }
                owner.resetAll();

                if (item.getDef().isMembers() && !Constants.GameServer.MEMBER_WORLD) {
                    owner.getActionSender().sendMessage("Nothing interesting happens.");
                    return;
                }

                switch (object.getID()) {
                
                    case 24: // Web
                        ItemWieldableDef def = item.getWieldableDef();
                        if ((def == null || def.getWieldPos() != 4) && item.getID() != 13) {
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                        }
                        owner.getActionSender().sendMessage("You try to destroy the web");
                        owner.setBusy(true);
                        World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                            public void action() {
                                if (Formulae.cutWeb()) {
                                    owner.getActionSender().sendMessage("You slice through the web.");
                                    world.unregisterGameObject(object);
                                    world.delayedSpawnObject(object.getLoc(), 15000);
                                } else {
                                    owner.getActionSender().sendMessage("You fail to cut through it.");
                                }
                                owner.setBusy(false);
                            }
                        });
                        break;
                    case 23: // Giant place near barb village
                        if (!itemId(new int[]{99})) {
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                        }
                        owner.getActionSender().sendMessage("You unlock the door and go through it");
                        doDoor();
                        if (owner.getY() <= 484) {
                            owner.teleport(owner.getX(), 485, false);
                        } else {
                            owner.teleport(owner.getX(), 484, false);
                        }
                        break;
                    case 60: // Melzars maze
                        if (!itemId(new int[]{421})) {
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                        }
                        owner.getActionSender().sendMessage("You unlock the door and go through it");
                        doDoor();
                        if (owner.getX() <= 337) {
                            owner.teleport(338, owner.getY(), false);
                        } else if (owner.getX() >= 338) {
                            owner.teleport(337, owner.getY(), false);
                        }
                        break;
                    default:
                        owner.getActionSender().sendMessage("Nothing interesting happens.");
                        return;
                }
                owner.getActionSender().sendInventory();
            }

            private void doDoor() {
                owner.getActionSender().sendSound("opendoor");
                world.registerGameObject(new GameObject(object.getLocation(), 11, object.getDirection(), object.getType()));
                world.delayedSpawnObject(object.getLoc(), 1000);
            }

            private boolean itemId(int[] ids) {
                return DataConversions.inArray(ids, item.getID());
            }
        });
    }

    private void handleObject(final Player player, final ActiveTile tile, final GameObject object, final InvItem item) {
        player.setStatus(Action.USING_INVITEM_ON_OBJECT);
        World.getWorld().getDelayedEventHandler().add(new WalkToObjectEvent(player, object, false) {
            public void arrived() {
                owner.resetPath();
                if (owner.isBusy() || owner.isRanging() || !owner.getInventory().contains(item) || !owner.nextTo(object) || !tile.hasGameObject() || !tile.getGameObject().equals(object) || owner.getStatus() != Action.USING_INVITEM_ON_OBJECT) {
                    return;
                }
                owner.resetAll();

                if (item.getDef().isMembers() && !Constants.GameServer.MEMBER_WORLD) {
                    owner.getActionSender().sendMessage("Nothing interesting happens.");
                    return;
                }

                String[] options;

                if (PluginHandler.getPluginHandler().blockDefaultAction("InvUseOnObject", new Object[]{object, item, owner}))
                    return;


                int[] range = {317, 254, 255, 256, 339, 324};

                if (object.getGameObjectDef().name.equalsIgnoreCase("fire")) {
                    for (int i : range) {
                        if (item.getID() == i) {
                            owner.getActionSender().sendMessage("You cannot cook this on a fire");
                            return;
                        }
                    }
                }
                int[] sinks = {48, 26, 86, 2, 466};
                if (item.getID() == 341) {
                    for (int i : sinks) {
                        if (i == object.getID()) {
                            if (owner.getInventory().remove(new InvItem(item.getID())) > -1) {
                                owner.getActionSender().sendSound("filljug");
                                owner.getActionSender().sendMessage("You fill up the bowl with water");
                                owner.getInventory().add(new InvItem(342));
                                owner.getActionSender().sendInventory();
                                return;
                            }
                        }
                    }
                }
                if (item.getID() == 132) {
                    if (object.getID() == 97 || object.getID() == 11 || object.getID() == 435) {
                        player.setBusy(true);
                        player.getActionSender().sendMessage("You cook the " + item.getDef().name + " on the " + object.getGameObjectDef().name);
                        player.getInventory().remove(132, 1);
                        player.getActionSender().sendInventory();
                        World.getWorld().getDelayedEventHandler().add(new MiniEvent(owner, 2000) {
                            public void action() {
                                player.getActionSender().sendMessage("You burn the " + item.getDef().name);
                                player.getInventory().add(new InvItem(134));
                                player.getActionSender().sendInventory();
                                player.setBusy(false);
                            }

                        });

                        return;
                    }
                }

                if (object.getX() == 233 && object.getY() == 180) {
                    if (item.getID() == 414) {
                        if (owner.getInventory().remove(new InvItem(414)) > -1) {
                            owner.setBusy(true);
                            owner.getActionSender().sendMessage("you open the secret chest..");
                            World.getWorld().getDelayedEventHandler().add(new MiniEvent(owner, 1000) {
                                public void action() {
                                    owner.setBusy(false);
                                    owner.getActionSender().sendMessage("you find treasure!");
                                    owner.getInventory().add(new InvItem(158));
                                    owner.getInventory().add(new InvItem(173));
                                    owner.getInventory().add(new InvItem(64));
                                    owner.getInventory().add(new InvItem(42, 2));
                                    owner.getInventory().add(new InvItem(38, 2));
                                    owner.getInventory().add(new InvItem(41, 10));
                                    owner.getInventory().add(new InvItem(10, 50));
                                    owner.getActionSender().sendInventory();
                                    return;
                                }
                            });

                        }
                    }
                }

                switch (object.getID()) {
                case 177: // dorics anvil
                case 50: // Anvil
      				int minSmithingLevel = Formulae.minSmithingLevel(item.getID());
      				if(minSmithingLevel < 0) {
      					owner.getActionSender().sendMessage("Nothing interesting happens.");
      					return;
      				}
      				if(owner.getInventory().countId(168) < 1) {
      					owner.getActionSender().sendMessage("You need a hammer to work the metal with.");
      					return;
      				}
      				if(owner.getCurStat(13) < minSmithingLevel) {
	      					owner.getActionSender().sendMessage("You need a smithing level of " + minSmithingLevel + " to use this type of bar");
	      					return;
	      				}
		      				options = new String[]{"Make Weapon", "Make Armour", "Make Missile Heads", "Cancel"};
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(int option, String reply) {
						if(owner.isBusy()) {
							return;
						}
						String[] options;
						switch(option) {
							case 0:
								owner.getActionSender().sendMessage("Choose a type of weapon to make");
								options = new String[]{"Dagger", "Throwing Knife", "Sword", "Axe", "Mace"};
								owner.setMenuHandler(new MenuHandler(options) {
									public void handleReply(int option, String reply) {
										if(owner.isBusy()) {
											return;
										}
										String[] options;
										switch(option) {
											case 0:
												handleSmithing(item.getID(), 0);
												break;
											case 1:
												if(!Constants.GameServer.MEMBER_WORLD) {
													player.getActionSender().sendMessage("This feature is members only");
													break;
												}
												handleSmithing(item.getID(), 1);
												break;
											case 2:
												owner.getActionSender().sendMessage("What sort of sword do you want to make?");
												options = new String[]{"Short Sword", "Long Sword (2 bars)", "Scimitar (2 bars)", "2-handed Sword (3 bars)"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy()) {
															return;
														}
														switch(option) {
															case 0:
																handleSmithing(item.getID(), 2);
																break;
															case 1:
																handleSmithing(item.getID(), 3);
																break;
															case 2:
																handleSmithing(item.getID(), 4);
																break;
															case 3:
																handleSmithing(item.getID(), 5);
																break;
															default:
																return;
														}
													}
												});
												owner.getActionSender().sendMenu(options);
												break;
											case 3:
												owner.getActionSender().sendMessage("What sort of axe do you want to make?");
												options = new String[]{"Hatchet", "Battle Axe (3 bars)"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy()) {
															return;
														}
														switch(option) {
															case 0:
																handleSmithing(item.getID(), 6);
																break;
															case 1:
																handleSmithing(item.getID(), 8);
																break;
															default:
																return;
														}
													}
												});
												owner.getActionSender().sendMenu(options);
												break;
											case 4:
												handleSmithing(item.getID(), 9);
												break;
											default:
												return;
										}
									}
								});
								owner.getActionSender().sendMenu(options);
								break;
							case 1:
								owner.getActionSender().sendMessage("Choose a type of armour to make");
								options = new String[]{"Helmet", "Shield", "Armour"};
								owner.setMenuHandler(new MenuHandler(options) {
									public void handleReply(int option, String reply) {
										if(owner.isBusy()) {
											return;
										}
										switch(option) {
											case 0:
												owner.getActionSender().sendMessage("What sort of helmet do you want to make?");
												options = new String[]{"Medium Helmet", "Large Helmet (2 bars)"};
												owner.setMenuHandler(new MenuHandler(options) {
													public void handleReply(int option, String reply) {
														if(owner.isBusy()) {
															return;
														}
														switch(option) {
															case 0:
																handleSmithing(item.getID(), 10);
																break;
															case 1:
																handleSmithing(item.getID(), 11);
																break;
																	default:
																		return;
																}
															}
														});
														owner.getActionSender().sendMenu(options);
														break;
													case 1:
														owner.getActionSender().sendMessage("What sort of shield do you want to make?");
														options = new String[]{"Square Shield (2 bars)", "Kite Shield (3 bars)"};
														owner.setMenuHandler(new MenuHandler(options) {
															public void handleReply(int option, String reply) {
																if(owner.isBusy()) {
																	return;
																}
																switch(option) {
																	case 0:
																		handleSmithing(item.getID(), 12);
																		break;
																	case 1:
																		handleSmithing(item.getID(), 13);
																		break;
																	default:
																		return;
																}
															}
														});
														owner.getActionSender().sendMenu(options);
														break;
													case 2:
														owner.getActionSender().sendMessage("What sort of armour do you want to make?");
														options = new String[]{"Chain Mail Body (3 bars)", "Plate Mail Body (5 bars)", "Plate Mail Legs (3 bars)", "Plated Skirt (3 bars)"};
														owner.setMenuHandler(new MenuHandler(options) {
															public void handleReply(int option, String reply) {
																if(owner.isBusy()) {
																	return;
																}
																switch(option) {
																	case 0:
																		handleSmithing(item.getID(), 14);
																		break;
																	case 1:
																		handleSmithing(item.getID(), 15);
																		break;
																	case 2:
																		handleSmithing(item.getID(), 16);
																		break;
																	case 3:
																		handleSmithing(item.getID(), 17);
																		break;
																	default:
																		return;
																}
															}
														});
														owner.getActionSender().sendMenu(options);
														break;
													default:
														return;
												}
											}
										});
										owner.getActionSender().sendMenu(options);
										break;
									case 2:
										
										if(!Constants.GameServer.MEMBER_WORLD) {
											player.getActionSender().sendMessage("This is a members feature only");
										break;
									}
										options = new String[]{"Make 10 Arrow Heads", "Make 50 Arrow Heads (5 bars)", "Forge Dart Tips", "Cancel"};
										owner.setMenuHandler(new MenuHandler(options) {
											public void handleReply(int option, String reply) {
												if(owner.isBusy()) {
													return;
												}
												switch(option) {
													case 0:
														handleSmithing(item.getID(), 18);
														break;
													case 1:
														handleSmithing(item.getID(), 19);
														break;
													case 2:
														handleSmithing(item.getID(), 20);
														break;
													default:
														return;
												}
											}
										});
										owner.getActionSender().sendMenu(options);
										break;
									default:
										return;
								}
							}
						});
						owner.getActionSender().sendMenu(options);
      				break;
      				/*
                    case 52: // hopper
                        if (item.getID() == 29) {

                            if (object.containsItem() == 29) {
                                owner.getActionSender().sendMessage("There is already grain in the hopper");
                                return;
                            }
                            if (owner.getInventory().remove(item) > -1) {
                                Bubble bubble = new Bubble(player, 29);
                                for (Player p : player.getViewArea().getPlayersInView()) {
                                    p.informOfBubble(bubble);
                                }
                                owner.getActionSender().sendMessage("You put the grain in the hopper");
                                object.containsItem(29);
                                owner.getActionSender().sendInventory();
                            }
                            return;
                        } */

                    case 282: // Fountain of Heroes
                        if (item.getID() == 522) {
                            owner.getActionSender().sendMessage("You dip the amulet in the fountain...");
                            owner.setBusy(true);
                            World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                                public void action() {
                                    owner.getActionSender().sendMessage("You feel more power coming from it than before.");
                                    World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                                        public void action() {
                                            if (owner.getInventory().remove(item) > -1) {
                                                owner.getActionSender().sendMessage("You can now rub it to teleport.");
                                                owner.getInventory().add(new InvItem(597));
                                                owner.getActionSender().sendInventory();
                                            }
                                            owner.setBusy(false);
                                        }
                                    });
                                }
                            });
                            break;
                        }
                    case 121: // Spinning Wheel
                        switch (item.getID()) {
                            case 145: // Wool
                                handleWoolSpinning();
                                break;
                            case 675: // Flax
                                handleFlaxSpinning();
                                break;
                            default:
                                owner.getActionSender().sendMessage("Nothing interesting happens.");
                                return;
                        }
                        owner.getActionSender().sendSound("mechanical");
                        break;
                    case 248: // Crystal key chest
                        if (item.getID() != 525) {
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                        }
                        owner.getActionSender().sendMessage("You use the key to unlock the chest");
                        owner.setBusy(true);
                        showBubble();
                        World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                            public void action() {
                                if (owner.getInventory().remove(item) > -1) {
                                    owner.getInventory().add(new InvItem(542, 1));
                                    List<InvItem> loot = Formulae.getKeyChestLoot();
                                    for (InvItem i : loot) {
                                        if (i.getAmount() > 1 && !i.getDef().isStackable()) {
                                            for (int x = 0; x < i.getAmount(); x++) {

                                                owner.getInventory().add(new InvItem(i.getID(), 1));
                                            }
                                        } else {
                                            if (i.getID() == 518 && i.getAmount() > 20) {
                                                i = new InvItem(518, DataConversions.random(0, 20) + 1);
                                            }
                                            if (i.getID() == 517 && i.getAmount() > 20) {
                                                i = new InvItem(517, DataConversions.random(0, 20) + 1);
                                            }
                                            Logger.println("Player: " + owner.getUsername() + " Got item: " + i.getID() + " From CHEST (" + i.getAmount() + ") sys time (" + System.currentTimeMillis() + ")");
                                            if (i.getAmount() > 4000) {
                                                Logger.println("WARNING!!!! Player: " + owner.getUsername() + " was about to get " + i.getAmount() + " of " + i.getID() + " from the CHEST sys time (" + System.currentTimeMillis() + ")");
                                                owner.setBusy(false);
                                                owner.getActionSender().sendInventory();
                                                return;
                                            }
                                            owner.getInventory().add(i);
                                        }
                                    }
                                    owner.getActionSender().sendInventory();
                                }
                                owner.setBusy(false);
                            }
                        });
                        break;
                    case 302: // Sandpit
                        if (item.getID() != 21) {
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                        }
                        owner.getActionSender().sendMessage("You fill the bucket with sand.");
                        owner.setBusy(true);
                        showBubble();
                        World.getWorld().getDelayedEventHandler().add(new MiniEvent(owner) {
                            public void action() {
                                if (owner.getInventory().remove(item) > -1) {
                                    owner.getInventory().add(new InvItem(625, 1));
                                    owner.getActionSender().sendInventory();
                                }
                                owner.setBusy(false);
                            }
                        });
                        break;
                    case 179: // Potters Wheel
                        if (item.getID() != 243) {
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                        }
                        owner.getActionSender().sendMessage("What would you like to make?");
                        options = new String[]{"Pot", "Pie Dish", "Bowl", "Cancel"};
                        owner.setMenuHandler(new MenuHandler(options) {
                            public void handleReply(int option, String reply) {
                                if (owner.isBusy()) {
                                    return;
                                }
                                int reqLvl, exp;
                                InvItem result;
                                switch (option) {
                                    case 0:
                                        result = new InvItem(279, 1);
                                        reqLvl = 1;
                                        exp = 6;
                                        break;
                                    case 1:
                                        result = new InvItem(278, 1);
                                        reqLvl = 4;
                                        exp = 10;
                                        break;
                                    case 2:
                                        result = new InvItem(340, 1);
                                        reqLvl = 7;
                                        exp = 10;
                                        break;
                                    default:
                                        owner.getActionSender().sendMessage("Nothing interesting happens.");
                                        return;
                                }
                                if (owner.getCurStat(12) < reqLvl) {
                                    owner.getActionSender().sendMessage("You need a crafting level of " + reqLvl + " to make this");
                                    return;
                                }
                                if (owner.getInventory().remove(item) > -1) {
                                    showBubble();
                                    owner.getActionSender().sendMessage("You make a " + result.getDef().getName());
                                    owner.getInventory().add(result);
                                    owner.incExp(12, exp, true);
                                    owner.getActionSender().sendStat(12);
                                    owner.getActionSender().sendInventory();
                                }
                            }
                        });
                        owner.getActionSender().sendMenu(options);
                        break;
                    case 178: // Potters Oven
                        int reqLvl,
                                xp,
                                resultID;
                        switch (item.getID()) {
                            case 279: // Pot
                                resultID = 135;
                                reqLvl = 1;
                                xp = 7;
                                break;
                            case 278: // Pie Dish
                                resultID = 251;
                                reqLvl = 4;
                                xp = 15;
                                break;
                            case 340: // Bowl
                                resultID = 341;
                                reqLvl = 7;
                                xp = 15;
                                break;
                            default:
                                owner.getActionSender().sendMessage("Nothing interesting happens.");
                                return;
                        }
                        if (owner.getCurStat(12) < reqLvl) {
                            owner.getActionSender().sendMessage("You need a crafting level of " + reqLvl + " to make this");
                            return;
                        }
                        final InvItem result = new InvItem(resultID, 1);
                        final int exp = xp;
                        final boolean fail = Formulae.crackPot(reqLvl, owner.getCurStat(12));
                        showBubble();
                        owner.getActionSender().sendMessage("You place the " + item.getDef().getName() + " in the oven");
                        owner.setBusy(true);
                        World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                            public void action() {
                                if (owner.getInventory().remove(item) > -1) {
                                    if (fail) {
                                        owner.getActionSender().sendMessage("The " + result.getDef().getName() + " cracks in the oven, you throw it away.");
                                    } else {
                                        owner.getActionSender().sendMessage("You take out the " + result.getDef().getName());
                                        owner.getInventory().add(result);
                                        owner.incExp(12, exp, true);
                                        owner.getActionSender().sendStat(12);
                                    }
                                    owner.getActionSender().sendInventory();
                                }
                                owner.setBusy(false);
                            }
                        });
                        break;
                    default:
                        //owner.getActionSender().sendMessage("Nothing interesting happens.");
                        return;
                }
            }
            
            public void handleSmithing(int barID, int toMake) {
				ItemSmithingDef def = EntityHandler.getSmithingDef((Formulae.getBarType(barID) * 21) + toMake);
				if(def == null) {
					owner.getActionSender().sendMessage("Nothing interesting happens.");
					return;
				}
				if(owner.getCurStat(13) < def.getRequiredLevel()) {
					owner.getActionSender().sendMessage("You need at smithing level of " + def.getRequiredLevel() + " to make this");
					return;
				}
				if(owner.getInventory().countId(barID) < def.getRequiredBars()) {
					owner.getActionSender().sendMessage("You don't have enough bars to make this.");
					return;
				}
				owner.getActionSender().sendSound("anvil");
				for(int x = 0;x < def.getRequiredBars();x++) {
					owner.getInventory().remove(new InvItem(barID, 1));
				}
	  		      	Bubble bubble = new Bubble(owner, item.getID());
				for(Player p : owner.getViewArea().getPlayersInView()) {
					p.informOfBubble(bubble);
				}
				if(EntityHandler.getItemDef(def.getItemID()).isStackable()) {
					owner.getActionSender().sendMessage("You hammer the metal into some " + EntityHandler.getItemDef(def.getItemID()).getName());
					owner.getInventory().add(new InvItem(def.getItemID(), def.getAmount()));
				}
				else {
					owner.getActionSender().sendMessage("You hammer the metal into a " + EntityHandler.getItemDef(def.getItemID()).getName());
					for(int x = 0;x < def.getAmount();x++) {
						owner.getInventory().add(new InvItem(def.getItemID(), 1));
					}
				}
				owner.incExp(13, Formulae.getSmithingExp(barID, def.getRequiredBars()), true);
				owner.getActionSender().sendStat(13);
				owner.getActionSender().sendInventory();
			}

            private void handleWoolSpinning() {
                showBubble();
                owner.getActionSender().sendMessage("You spin the sheeps wool into a nice ball of wool");
                World.getWorld().getDelayedEventHandler().add(new MiniEvent(owner) {
                    public void action() {
                        if (owner.getInventory().remove(item) > -1) {
                            owner.getInventory().add(new InvItem(207, 1));
                            owner.incExp(12, 3, true);
                            owner.getActionSender().sendStat(12);
                            owner.getActionSender().sendInventory();
                        }
                        owner.setBusy(false);
                        if (owner.getInventory().hasItemId(item.getID())) {
                            handleWoolSpinning();
                        }
                    }
                });
            }

            private void handleFlaxSpinning() {
                if (owner.getCurStat(12) < 10) {
                    owner.getActionSender().sendMessage("You need a crafting level of 10 to spin flax!");
                    owner.setBusy(false);
                    return;
                }
                showBubble();
                owner.getActionSender().sendMessage("You make the flax into a bow string");
                World.getWorld().getDelayedEventHandler().add(new MiniEvent(owner) {
                    public void action() {
                        if (owner.getInventory().remove(item) > -1) {
                            owner.getInventory().add(new InvItem(676, 1));
                            owner.incExp(12, 15, true);
                            owner.getActionSender().sendStat(12);
                            owner.getActionSender().sendInventory();
                        }
                        owner.setBusy(false);
                        if (owner.getInventory().hasItemId(item.getID())) {
                            handleFlaxSpinning();
                        }
                    }
                });
            }


            private void showBubble() {
                Bubble bubble = new Bubble(owner, item.getID());
                for (Player p : owner.getViewArea().getPlayersInView()) {
                    p.informOfBubble(bubble);
                }
            }
        });
    }

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();
        int pID = ((RSCPacket) p).getID();
        if (player.isBusy()) {
            player.resetPath();// sendSound
            return;
        }
        player.resetAll();
        ActiveTile tile = world.getTile(p.readShort(), p.readShort());
        if (tile == null) {
            player.setSuspiciousPlayer(true);
            player.resetPath();
            return;
        }
        GameObject object = tile.getGameObject();
        InvItem item;
        switch (pID) {
            case 36: // Use Item on Door
                int dir = p.readByte();
                item = player.getInventory().get(p.readShort());
                if (object == null || object.getType() == 0 || item == null) { // This
                    // shoudln't
                    // happen
                    player.setSuspiciousPlayer(true);
                    return;
                }

                //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used item " + item + " on door at " + player.getLocation()));
                handleDoor(player, tile, object, dir, item);
                break;
            case 94: // Use Item on GameObject
                item = player.getInventory().get(p.readShort());
                if (object == null || object.getType() == 1 || item == null) { // This
                    // shoudln't
                    // happen
                    player.setSuspiciousPlayer(true);
                    return;
                }

                //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used item " + item + " on object at " + player.getLocation()));
                handleObject(player, tile, object, item);
                break;
        }
        tile.cleanItself();
    }

}
