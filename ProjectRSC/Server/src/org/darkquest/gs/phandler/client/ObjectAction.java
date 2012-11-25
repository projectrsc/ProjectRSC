package org.darkquest.gs.phandler.client;

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
					
					if(object.getID() == 331 && object.getX() == 150 && object.getY() == 558) { // upstairs champs
						owner.teleport(151, 1505, false);
						return;
					}
					
					if(object.getID() == 6 && object.getX() == 148 && object.getY() == 1507) { // escape ladder champs
						owner.teleport(148, 563, false);
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
}
