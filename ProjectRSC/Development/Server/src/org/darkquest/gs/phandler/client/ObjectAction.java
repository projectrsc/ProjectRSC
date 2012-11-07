package org.darkquest.gs.phandler.client;

import org.darkquest.config.Constants;
import org.darkquest.config.Formulae;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.connection.RSCPacket;
import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.event.impl.WalkToObjectEvent;
import org.darkquest.gs.external.EntityHandler;
import org.darkquest.gs.external.GameObjectDef;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.model.TelePoint;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.states.Action;
import org.darkquest.gs.world.ActiveTile;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


public class ObjectAction implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel session) {

		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}


		player.resetAll();
		ActiveTile t = world.getTile(p.readShort(), p.readShort());
		if(t == null)
			return;
		final GameObject object = t.getGameObject();
		final int click = pID == 51 ? 0 : 1;
		player.click = click;
		if (object == null) {
			t.cleanItself();
			player.setSuspiciousPlayer(true);
			return;
		}

		/**
		 * Limit places that people can access!
		 */
		if (object.getX() == 243 && object.getY() == 178)
			return;
		if (object.getX() == 59 && object.getY() == 573)
			return;

		if (object.getX() == 94 && object.getY() == 521 && object.getID() == 60) {// varrock tea stall area
			player.sendMemberErrorMessage();
			return;
		}

		//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used object " + object.getID() + " at " + player.getLocation()));

		player.setStatus(Action.USING_OBJECT);
		World.getWorld().getDelayedEventHandler().add(new WalkToObjectEvent(player, object, false) {
			public void arrived() {
				try {
					owner.resetPath();

					GameObjectDef def = object.getGameObjectDef();
					if (owner.isBusy() || owner.isRanging() || !owner.nextTo(object) || def == null || owner.getStatus() != Action.USING_OBJECT) {
						return;
					}
					
					//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(owner.getUsername() + " walked to object " + object.getID() + " at " + owner.getLocation()));

					owner.resetAll();

					String command = (click == 0 ? def.getCommand1() : def.getCommand2()).toLowerCase();

					if (PluginHandler.getPluginHandler().blockDefaultAction("ObjectAction", new Object[]{object, command, owner})) {
						return;
					}

					TelePoint telePoint = EntityHandler.getObjectTelePoint(object.getLocation(), command);
					if (telePoint != null) {
						if (telePoint.getCommand().equalsIgnoreCase("pull") && owner.getLocation().inWilderness() && System.currentTimeMillis() - owner.getLastMoved() < 10000) {
							owner.getActionSender().sendMessage("You have to stand still for 10 seconds to use the lever.");
							return;
						}
						owner.teleport(telePoint.getX(), telePoint.getY(), false);
					}


					if (object.getID() == 198 && object.getX() == 251 && object.getY() == 468) { // Prayer Guild Ladder
						if (owner.getMaxStat(5) < 31) {
							owner.setBusy(true);
							Npc abbot = World.getWorld().getNpc(174, 249, 252, 458, 468);
							if (abbot != null) {
								owner.informOfNpcMessage(new ChatMessage(abbot, "Hello only people with high prayer are allowed in here", owner));
							} else {
								return;
							}
							World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
								public void action() {
									owner.setBusy(false);
									owner.getActionSender().sendMessage("You need a prayer level of 31 to enter");
								}
							});
						} else {
							owner.teleport(251, 1411, false);
						}
						return;
					} else if (object.getID() == 223 && object.getX() == 274 && object.getY() == 566) { // Mining Guild Ladder
						if (owner.getCurStat(14) < 60) {
							owner.setBusy(true);
							Npc dwarf = World.getWorld().getNpc(191, 272, 277, 563, 567);
							if (dwarf != null) {
								owner.informOfNpcMessage(new ChatMessage(dwarf, "Hello only the top miners are allowed in here", owner));
							}
							World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
								public void action() {
									owner.setBusy(false);
									owner.getActionSender().sendMessage("You need a mining level of 60 to enter");
								}
							});
						} else {
							owner.teleport(274, 3397, false);
						}
						return;
					}

					if (object.getID() == 1187 && object.getX() == 446 && object.getY() == 3367) { // Mage bank Ladder
						owner.teleport(222, 110, false);
						return;
					}
					if (command.equals("climb-up") || command.equals("climb up") || command.equals("go up")) {
						int[] coords = coordModifier(owner, true, object);
						owner.teleport(coords[0], coords[1], false);
						return;
					} else if (command.equals("climb-down") || command.equals("climb down") || command.equals("go down")) {
						int[] coords = coordModifier(owner, false, object);
						owner.teleport(coords[0], coords[1], false);
						return;
					}

					if (command.equalsIgnoreCase("close") || command.equalsIgnoreCase("open")) {
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
							if (object.getX() == 285 || object.getY() == 185) { // Lesser demon gate
								doGate(owner, object);
								if (owner.getX() <= 284) {
									owner.teleport(285, 185, false);
								} else {
									owner.teleport(284, 185, false);
								}
							}
							break;//handleRefill
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
					}
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private int[] coordModifier(Player player, boolean up, GameObject object) {
		if (object.getGameObjectDef().getHeight() <= 1) {
			return new int[]{player.getX(), Formulae.getNewY(player.getY(), up)};
		}
		int[] coords = {object.getX(), Formulae.getNewY(object.getY(), up)};
		switch (object.getDirection()) {
		case 0:
			coords[1] -= (up ? -object.getGameObjectDef().getHeight() : 1);
			break;
		case 2:
			coords[0] -= (up ? -object.getGameObjectDef().getHeight() : 1);
			break;
		case 4:
			coords[1] += (up ? -1 : object.getGameObjectDef().getHeight());
			break;
		case 6:
			coords[0] += (up ? -1 : object.getGameObjectDef().getHeight());
			break;
		}
		return coords;
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
}
