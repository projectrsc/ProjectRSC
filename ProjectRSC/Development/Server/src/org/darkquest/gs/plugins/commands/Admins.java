package org.darkquest.gs.plugins.commands;

import org.darkquest.gs.db.query.StaffLog;
import org.darkquest.gs.event.SingleEvent;
import org.darkquest.gs.external.EntityHandler;
import org.darkquest.gs.external.ItemLoc;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.Mob;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.model.Point;
import org.darkquest.gs.plugins.listeners.action.CommandListener;
import org.darkquest.gs.service.Services;
import org.darkquest.gs.states.CombatState;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.World;

public final class Admins implements CommandListener {

	private final World world = World.getWorld();

	private static final String[] towns = { "varrock", "falador", "draynor", "portsarim", "karamja", "alkharid", "lumbridge", "edgeville", "castle", "taverly", "clubhouse", "seers", "barbarian", "rimmington", "catherby", "ardougne", "yanille", "lostcity", "gnome" };

	private static final Point[] townLocations = { Point.location(122, 509), Point.location(304, 542), Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693), Point.location(120, 648), Point.location(217, 449), Point.location(270, 352), Point.location(373, 498), Point.location(653, 491), Point.location(501, 450), Point.location(233, 513), Point.location(325, 663), Point.location(440, 501), Point.location(549, 589), Point.location(583, 747), Point.location(127, 3518), Point.location(703, 527) };

	private static final String COMMAND_PREFIX = "@red@SERVER: @whi@";

	@Override
	public void onCommand(String command, String[] args, Player player) {
		if (!player.isAdmin()) {
			return;
		}
		else if (command.equals("fatigue")) {
			player.setFatigue(7500);
			player.getActionSender().sendFatigue(player.getFatigue() / 10);
		} else if (command.equals("update")) {
			String reason = "";
			int seconds = 60;
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					if (i == 0) {
						try {
							seconds = Integer.parseInt(args[i]);
						} catch (Exception e) {
							reason += (args[i] + " ");
						}
					} else {
						reason += (args[i] + " ");
					}
				}
				reason = reason.substring(0, reason.length() - 1);
			}
			int minutes = seconds / 60;
			int remainder = seconds % 60;

			if (world.getServer().shutdownForUpdate(seconds)) {
				String message = "The server will be shutting down in " + (minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") + " " : "") + (remainder > 0 ? remainder + " second" + (remainder > 1 ? "s" : "") : "") + (reason == "" ? "" : ": % % " + reason);
				for (Player p : world.getPlayers()) {
					p.getActionSender().sendAlert(message, false);
					p.getActionSender().startShutdown(seconds);
				}
			}
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " used UPDATE " + minutes + ":" + remainder + " " + reason));
		} else if (command.equals("appearance")) {
			player.setChangingAppearance(true);
			player.getActionSender().sendAppearanceScreen();
		}  else if (command.equals("pos")) {
			player.getActionSender().sendMessage("X: " + player.getX() + ", Y: " + player.getY());
		} else if (command.equals("npc")) {
			if (args.length < 1) {
				sendInvalidArguments(player, "npc", "id", "[store=true/false]");
				return;
			}
			boolean percist = (args.length > 1 ? args[1].equalsIgnoreCase("true") : false);

			int id = Integer.parseInt(args[0]);
			if (EntityHandler.getNpcDef(id) != null) {
				final Npc n = new Npc(id, player.getX(), player.getY(), player.getX() - 5, player.getX() + 5, player.getY() - 5, player.getY() + 5);
				n.setRespawn(percist);
				world.registerNpc(n);
				if(!percist) {
					world.getDelayedEventHandler().add(new SingleEvent(null, 60000) {
						public void action() {
							Mob opponent = n.getOpponent();
							if (opponent != null) {
								opponent.resetCombat(CombatState.ERROR);
							}
							n.resetCombat(CombatState.ERROR);
							world.unregisterNpc(n);
							n.remove();
						}
					});
				} else {
					world.getDB().storeNpcToDatabase(n);
					player.getActionSender().sendMessage(COMMAND_PREFIX + "Npc stored to the database.");
				}
				//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " spawned a " + n.getDef().getName() + " at " + player.getLocation().toString() + ", percistant = " + percist));
			} else {
				player.getActionSender().sendMessage(COMMAND_PREFIX + "Invalid id");
			}
		} else if (command.equals("dropall")) {
			player.getInventory().getItems().clear();
			player.getActionSender().sendInventory();
		} else if (command.equals("sysmsg")) {
			StringBuilder sb = new StringBuilder("@ran@SYSTEM MESSAGE: @whi@");

			for (int i = 0; i < args.length; i++) {
				sb.append(args[i]).append(" ");
			}

			world.sendWorldMessage(sb.toString());
			world.sendWorldMessage(sb.toString());
			world.sendWorldMessage(sb.toString());
			world.sendWorldMessage(sb.toString());
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " used SYSMSG " + sb.toString()));
		} else if (command.equals("info")) {
			if (args.length != 1) {
				sendInvalidArguments(player, "info", "name");
				return; 
			}

			world.getServer().getLoginConnector().getActionSender().requestPlayerInfo(player, DataConversions.usernameToHash(args[0]));
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " used INFO " + args[0]));
		} else if (command.equals("say")) {
			String newStr = "@whi@";

			for (int i = 0; i < args.length; i++) {
				newStr += args[i] + " ";
			}

			newStr = player.getRankHeader() + player.getUsername() + ": " + newStr;

			World.getWorld().sendWorldMessage(newStr);
			// World.getWorld().addEntryToSnapshots(new Chatlog(player.getUsername(), "(Global) " + newStr, new ArrayList<String>()));
			return;
		}
		if (command.equals("shutdown")) {
			System.out.println(player.getUsername() + " shut down the server!");
			World.getWorld().getServer().kill();
			return;
		}
	}


		private void sendInvalidArguments(Player p, String... strings) {
			StringBuilder sb = new StringBuilder(COMMAND_PREFIX + "Invalid arguments @red@Syntax: @whi@");

			for (int i = 0; i < strings.length; i++) {
				sb.append(i == 0 ? strings[i].toUpperCase() : strings[i]).append(i == (strings.length - 1) ? "" : " ");
			}
			p.getActionSender().sendMessage(sb.toString());
		}

	}
