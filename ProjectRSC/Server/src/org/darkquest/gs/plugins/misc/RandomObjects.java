package org.darkquest.gs.plugins.misc;

import org.darkquest.config.Constants;
import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import org.darkquest.gs.world.World;

public class RandomObjects implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(final GameObject object, String command, Player owner) {	
		if (command.equals("search") && object.getGameObjectDef().getName().equals("cupboard")) {
			owner.getActionSender().sendMessage("You search the " + object.getGameObjectDef().getName() + "...");
			World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
				public void action() {
					if (object.getX() == 216 && object.getY() == 1562) {
						owner.getActionSender().sendMessage("You find Garlic!");
						owner.getInventory().add(new InvItem(218));
						owner.getActionSender().sendInventory();
					} else {
						owner.getActionSender().sendMessage("You find nothing");
					}
				}
			});
			return;
		} else if (command.equals("board")) {
			owner.getActionSender().sendMessage("You must talk to the owner about this.");
			return;
		} else if (command.equalsIgnoreCase("close") || command.equalsIgnoreCase("open")) {
			switch (object.getID()) {
			case 18:
				replaceGameObject(17, true, owner, object);
				break;
			case 17:
				replaceGameObject(18, false, owner, object);
				break;
			case 58:
				replaceGameObject(57, false, owner, object);
				break;
			case 57:
				replaceGameObject(58, true, owner, object);
				break;
			case 63:
				replaceGameObject(64, false, owner, object);
				break;
			case 64:
				replaceGameObject(63, true, owner, object);
				break;
			case 79:
				replaceGameObject(78, false, owner, object);
				break;
			case 78:
				replaceGameObject(79, true, owner, object);
				break;
			case 60:
				replaceGameObject(59, true, owner, object);
				break;
			case 59:
				replaceGameObject(60, false, owner, object);
				break;
			case 135:
				replaceGameObject(136, true, owner, object);
				break;
			case 136:
				replaceGameObject(135, false, owner, object);
				break;
			case 137: // Members Gate (Doriks)
				if (object.getX() != 341 || object.getY() != 487) {
					return;
				}
				if (!Constants.GameServer.MEMBER_WORLD) {
					owner.getActionSender().sendMessage("You need to be on a members server to use this gate");
					return;
				}
				doGate(owner, object);
				if (owner.getX() <= 341) {
					owner.teleport(342, 487, false);
				} else {
					owner.teleport(341, 487, false);
				}
				break; 
					
			case 138: // Members Gate (Crafting Guild)
				if (object.getX() != 343 || object.getY() != 581) {
					return;
				}
				if (!Constants.GameServer.MEMBER_WORLD) {
					owner.getActionSender().sendMessage("You need to be on a members server to use this gate");
					return;
				}
				doGate(owner, object);
				if (owner.getY() <= 580) {
					owner.teleport(343, 581, false);
				} else {
					owner.teleport(343, 580, false);
				}
				break;
			case 180: // Al-Kharid Gate
				if (object.getX() != 92 || object.getY() != 649) {
					return;
				}
				doGate(owner, object);
				if (owner.getX() <= 91) {
					owner.teleport(92, 649, false);
				} else {
					owner.teleport(91, 649, false);
				}
				break;
			case 254: // Karamja Gate
				if (!Constants.GameServer.MEMBER_WORLD) {
					owner.getActionSender().sendMessage("Nothing interesting happens.");
					return;
				}
				if (object.getX() != 434 || object.getY() != 682) {
					return;
				}
				doGate(owner, object);
				if (owner.getX() <= 434) {
					owner.teleport(435, 682, false);
				} else {
					owner.teleport(434, 682, false);
				}
				break;
			case 563: // King Lanthlas Gate
				if (object.getX() != 660 || object.getY() != 551) {
					return;
				}
				doGate(owner, object);
				if (owner.getY() <= 551) {
					owner.teleport(660, 552, false);
				} else {
					owner.teleport(660, 551, false);
				}
				break;
			case 626: // Gnome Stronghold Gate
				if (object.getX() != 703 || object.getY() != 531) {
					return;
				}
				doGate(owner, object);
				if (owner.getY() <= 531) {
					owner.teleport(703, 532, false);
				} else {
					owner.teleport(703, 531, false);
				}
				break;
			case 305: // Edgeville Members Gate
				if (!Constants.GameServer.MEMBER_WORLD) {
					owner.getActionSender().sendMessage("Nothing interesting happens.");
					return;
				}
				if (object.getX() != 196 || object.getY() != 3266) {
					return;
				}
				doGate(owner, object);
				if (owner.getY() <= 3265) {
					owner.teleport(196, 3266, false);
				} else {
					owner.teleport(196, 3265, false);
				}
				break;
			case 1089: // Dig Site Gate
				if (object.getX() != 59 || object.getY() != 573) {
					return;
				}
				doGate(owner, object);
				if (owner.getX() <= 58) {
					owner.teleport(59, 573, false);
				} else {
					owner.teleport(58, 573, false);
				}
				break;
			case 356: // Woodcutting Guild Gate
				if (object.getX() != 560 || object.getY() != 472) {
					return;
				}
				if (owner.getY() <= 472) {
					doGate(owner, object);
					owner.teleport(560, 473, false);
				} else {
					if (owner.getCurStat(8) < 70) {
						owner.setBusy(true);
						Npc mcgrubor = World.getWorld().getNpc(255, 556, 564, 473, 476);
						if (mcgrubor != null) {
							owner.informOfNpcMessage(new ChatMessage(mcgrubor, "Hello only the top woodcutters are allowed in here", owner));
						}
						World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.setBusy(false);
								owner.getActionSender().sendMessage("You need a woodcutting level of 70 to enter");
							}
						});
					} else {
						doGate(owner, object);
						owner.teleport(560, 472, false);
					}
				}
				break;
			case 142: // Black Knight Big Door
				owner.getActionSender().sendMessage("The doors are locked");
				break;
			case 93: // Red dragon gate
				if (!Constants.GameServer.MEMBER_WORLD) {
					owner.getActionSender().sendMessage("Nothing interesting happens.");
					return;
				}
				if (object.getX() != 140 || object.getY() != 180) {
					return;
				}
				doGate(owner, object);
				if (owner.getY() <= 180) {
					owner.teleport(140, 181, false);
				} else {
					owner.teleport(140, 180, false);
				}
				break;
			case 508:
				if (!Constants.GameServer.MEMBER_WORLD) {
					owner.getActionSender().sendMessage("Nothing interesting happens.");
					return;
				}
				if (object.getX() == 111 && object.getY() == 142) {
					doGate(owner, object);
					if (owner.getY() == 142) {
						owner.teleport(owner.getX(), 141, false);
					} else {
						owner.teleport(owner.getX(), 142, false);
					}
				}
				
				break;//handleRefill
						case 508: // Lesser demon gate
				if (!Constants.GameServer.MEMBER_WORLD) {
					owner.getActionSender().sendMessage("Nothing interesting happens.");
					return;
				}
				if (object.getX() == 285 || object.getY() == 185) {
					return;
				}
				doGate(owner, object);
		if (owner.getX() <= 284) {
						owner.teleport(285, 185, false);
					} else {
						owner.teleport(284, 185, false);
				}
				break;
			case 319: // Lava Maze Gate
				if (object.getX() != 243 || object.getY() != 178) {
					return;
				}
				doGate(owner, object);
				if (owner.getY() <= 178) {
					owner.teleport(243, 179, false);
				} else {
					owner.teleport(243, 178, false);
				}
				break;
			case 712: // Shilo inside gate
				if (object.getX() != 394 || object.getY() != 851) {
					return;
				}
				owner.teleport(383, 851, false);
				break;
			case 611: // Shilo outside gate
				if (object.getX() != 388 || object.getY() != 851) {
					return;
				}
				owner.teleport(394, 851, false);
				break;
			case 1079: // Legends guild gate
				if (object.getX() != 512 || object.getY() != 550) {
					return;
				}
				doGate(owner, object);
				if (owner.getY() <= 550) {
					owner.teleport(513, 551, false);
				} else {
					owner.teleport(513, 550, false);
				}
				break;
			default:
				//owner.getActionSender().sendMessage("Nothing interesting happens.");
				return;
			}
		} else {
			switch (object.getID()) {
			case 613: // Shilo cart
				if (object.getX() != 384 || object.getY() != 851) {
					return;
				}
				owner.setBusy(true);
				owner.getActionSender().sendMessage("You search for a way over the cart");
				World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.getActionSender().sendMessage("You climb across");
						if (owner.getX() <= 383) {
							owner.teleport(386, 851, false);
						} 
						else {
							owner.teleport(383, 851, false);
						}
						owner.setBusy(false);
					}
				});
				break;
			case 643: // Gnome tree stone
				if (object.getX() != 416 || object.getY() != 161) {
					return;
				}
				owner.setBusy(true);
				owner.getActionSender().sendMessage("You twist the stone tile to one side");
				World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.getActionSender().sendMessage("It reveals a ladder, you climb down");
						owner.teleport(703, 3284, false);
						owner.setBusy(false);
					}
				});
				break;
			case 638: // First roots in gnome cave
				if (object.getX() != 701 || object.getY() != 3280) {
					return;
				}
				owner.setBusy(true);
				owner.getActionSender().sendMessage("You push the roots");
				World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.getActionSender().sendMessage("They wrap around you and drag you forwards");
						owner.teleport(701, 3278, false);
						owner.setBusy(false);
					}
				});
			case 639: // Second roots in gnome cave
				if (object.getX() != 701 || object.getY() != 3279) {
					return;
				}
				owner.setBusy(true);
				owner.getActionSender().sendMessage("You push the roots");
				World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.getActionSender().sendMessage("They wrap around you and drag you forwards");
						owner.teleport(701, 3281, false);
						owner.setBusy(false);
					}
				});
				break;
			}

			if (object.getX() == 94 && object.getY() == 521 && object.getID() == 60) {
				try {
					int x = owner.getX() == 94 ? 93 : 94, y = owner.getY();
					owner.teleport(x, y, false);
					//System.out.println(x + ", " + y);
					owner.getActionSender().sendMessage("@red@If this is still here by release, please contact Hikilaka to fix it!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}
	}
	
	private void replaceGameObject(int newID, boolean open, Player owner, GameObject object) {
		World.getWorld().registerGameObject(new GameObject(object.getLocation(), newID, object.getDirection(), object.getType()));
		owner.getActionSender().sendSound(open ? "opendoor" : "closedoor");
	}

	private void doGate(Player owner, GameObject object) {
		owner.getActionSender().sendSound("opendoor");
		World.getWorld().registerGameObject(new GameObject(object.getLocation(), 181, object.getDirection(), object.getType()));
		World.getWorld().delayedSpawnObject(object.getLoc(), 1000);
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) { // FIX
		if (obj.getID() == 613 || obj.getID() == 638 || obj.getID() == 639 || obj.getID() == 643
				|| command.equals("board") || command.startsWith("search") && obj.getID() != 136 
				|| command.equalsIgnoreCase("open") || command.equalsIgnoreCase("close")) {
			return true;
		}
		//System.out.println(obj.getX() + ", " + obj.getY() + ", " + obj.getID());
		
		if (obj.getLocation().getX() == 94 && obj.getLocation().getY() == 521 && obj.getID() == 60) {
			if (Constants.GameServer.MEMBER_WORLD) {
				return true;
			}
		}
		return false;
	}

}
