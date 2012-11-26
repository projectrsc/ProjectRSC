package org.darkquest.gs.plugins.commands;

import java.sql.ResultSet;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.darkquest.config.Constants;
import org.darkquest.config.Formulae;
import org.darkquest.gs.connection.filter.ConnectionFilter;
import org.darkquest.gs.db.DatabaseManager;
import org.darkquest.gs.db.query.StaffLog;
import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.event.SingleEvent;
import org.darkquest.gs.external.EntityHandler;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.Mob;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.model.Point;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.plugins.listeners.action.CommandListener;
import org.darkquest.gs.service.Services;
import org.darkquest.gs.states.CombatState;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.ActiveTile;
import org.darkquest.gs.world.TileValue;
import org.darkquest.gs.world.World;

public final class Admins implements CommandListener {

	private final World world = World.getWorld();

	private static final String[] towns = { "varrock", "falador", "draynor", "portsarim", "karamja", "alkharid", "lumbridge", "edgeville", "castle", "taverly", "clubhouse", "seers", "barbarian", "rimmington", "catherby", "ardougne", "yanille", "lostcity", "gnome" };

	private static final Point[] townLocations = { Point.location(122, 509), Point.location(304, 542), Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693), Point.location(120, 648), Point.location(217, 449), Point.location(270, 352), Point.location(373, 498), Point.location(653, 491), Point.location(501, 450), Point.location(233, 513), Point.location(325, 663), Point.location(440, 501), Point.location(549, 589), Point.location(583, 747), Point.location(127, 3518), Point.location(703, 527) };

	private static final String COMMAND_PREFIX = "@red@SERVER: @whi@";

	private DelayedEvent maskEvent;

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
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " used UPDATE " + minutes + ":" + remainder + " " + reason));
		} else if (command.equals("appearance")) {
			player.setChangingAppearance(true);
			player.getActionSender().sendAppearanceScreen();
		}  else if (command.equals("pos")) {
			player.getActionSender().sendMessage("X: " + player.getX() + ", Y: " + player.getY());
		} else if(command.equals("resetq")) {
			int quest = Integer.parseInt(args[0]);
			int stage = Integer.parseInt(args[1]);
			player.setQuestStage(quest, stage);
		} else if (command.equals("dropall")) {
			player.getInventory().getItems().clear();
			player.getActionSender().sendInventory();
		} else if (command.equals("sysmsg")) {
			StringBuilder sb = new StringBuilder("SYSTEM MESSAGE: @whi@");

			for (int i = 0; i < args.length; i++) {
				sb.append(args[i]).append(" ");
			}

			world.sendWorldMessage("@red@" + sb.toString());
			world.sendWorldMessage("@yel@" + sb.toString());
			world.sendWorldMessage("@gre@" + sb.toString());
			world.sendWorldMessage("@cya@" + sb.toString());
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " used SYSMSG " + sb.toString()));
		} else if (command.equals("system")) {
			StringBuilder sb = new StringBuilder("@yel@System message: @whi@");

			for (int i = 0; i < args.length; i++) {
				sb.append(args[i]).append(" ");
			}

			for (Player p : World.getWorld().getPlayers()) {
				p.getActionSender().sendAlert(sb.toString(), false);
			}
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " used SYSTEM " + sb.toString()));
		} else if (command.equals("removeip")) {
			if (args.length != 1) {
				sendInvalidArguments(player, "removeip", "ip");
				return; 
			}
			String ip = args[0];
			long hashed = DataConversions.IPToLong(ip);
			// NEED TO CREATE A PACKET FOR THIS EVENT TO HAPPEN
			if(ConnectionFilter.getInstance() != null && ConnectionFilter.getInstance().getCurrentBans().contains(hashed)) {
				ConnectionFilter.getInstance().toBlacklist(ip, false);
				ConnectionFilter.getInstance().getCurrentClients().remove(hashed);
				ConnectionFilter.getInstance().getCurrentBans().remove(hashed);
				player.getActionSender().sendMessage("Removed " + ip + " from filter");
			} else {
				player.getActionSender().sendMessage(ip + " does not exist");
			}
		} else if (command.equalsIgnoreCase("raiselimit")) { 
			if(ConnectionFilter.getInstance() != null) {
				ConnectionFilter.getInstance().adjustLimit(Integer.parseInt(args[0]));
				player.getActionSender().sendMessage("Adjusted threshold limit to: " + args[0]);
			}
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
		} else if (command.equals("ban") || command.equals("unban")) {
			boolean banned = command.equals("ban");
			if (args.length != 1) {
				sendInvalidArguments(player, banned ? "ban" : "unban", "name");
				return;
			}
			world.getServer().getLoginConnector().getActionSender().banPlayer(player, DataConversions.usernameToHash(args[0]), banned);
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " attempted to " + (banned ? "banned" : "unbanned") + " " + args[0]));
		} else if (command.equalsIgnoreCase("town")) {
			try {
				String town = args[0];
				if (town != null) {
					for (int i = 0; i < towns.length; i++)
						if (town.equalsIgnoreCase(towns[i])) {
							player.teleport(townLocations[i].getX(), townLocations[i].getY(), true);
							//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " went to " + args[0]));
							return;
						}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (command.equals("blink")) {
			player.setBlink(!player.blink());
			player.getActionSender().sendMessage(COMMAND_PREFIX + "Your blink status is now " + player.blink());
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " changed blink status to " + player.blink()));
		} else if (command.equals("invis")) {
			if (player.isInvis()) {
				player.setinvis(false);
			} else {
				player.setinvis(true);
			}
			player.getActionSender().sendMessage(COMMAND_PREFIX + "You are now " + (player.isInvis() ? "invisible" : "visible"));
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " went " + (player.isInvis() ? "in" : "") + "visible"));
		} else if (command.equals("teleport")) {
			if (args.length != 2) {
				player.getActionSender().sendMessage("Invalid args. Syntax: TELEPORT x y");
				return;
			}
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			if (world.withinWorld(x, y)) {
				//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " teleported from " + player.getLocation().toString() + " to (" + x + ", " + y + ")"));
				player.teleport(x, y, true);
			} else {
				player.getActionSender().sendMessage("Invalid coordinates!");
			}
		} else if (command.equals("reload")) {
			boolean failed = false;
			if(PluginHandler.getPluginHandler() != null) {
				player.getActionSender().sendMessage("Reloading all script factories...");
				if(PluginHandler.getPluginHandler().getPythonScriptFactory().canReload())
					try {
						for(Player pl : World.getWorld().getPlayers()) {
							pl.setBusy(true);
							pl.setBusy(false);
						}
						PluginHandler.getPluginHandler().loadPythonScripts();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						for(Player pl : World.getWorld().getPlayers()) {
							pl.getActionSender().sendQuestInfo();
						}
						if(!PluginHandler.getPluginHandler().getPythonScriptFactory().getErrorLog().isEmpty()) {
							failed = true;
							String errorFound = "@cya@[@whi@DEBUGGER v1@cya@]: ";
							for(String[] error : PluginHandler.getPluginHandler().getPythonScriptFactory().getErrorLog()) {
								String line = "";
								for(String er : error) {
									line += er;
								}
								errorFound += line + " ";
							}
							player.getActionSender().sendAlert(errorFound, false);
							PluginHandler.getPluginHandler().getPythonScriptFactory().getErrorLog().clear();
						}
					}
				if(!failed)
					player.getActionSender().sendMessage("Complete...");
				else
					player.getActionSender().sendMessage("@cya@[@whi@DEBUG@cya@]: @red@Error found while compiling scripts");
			}
		} else if (command.equals("check")) {
			if (args.length < 1) {
				sendInvalidArguments(player, "check", "name");
				return;
			}
			long hash = DataConversions.usernameToHash(args[0]);
			String currentIp = null;
			Player target = World.getWorld().getPlayer(hash);

			if (target == null) {
				player.getActionSender().sendMessage(COMMAND_PREFIX + "No online character found named '" + args[0] + "'.. checking MySQL..");

				try {
					Statement statement = World.getWorld().getDB().getConnection().createStatement();
					ResultSet result = statement.executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` WHERE `user`=" + hash);

					if (result.next()) {
						currentIp = result.getString("login_ip");
					} else {
						player.getActionSender().sendMessage(COMMAND_PREFIX + "Error character not found in MySQL");
						return;
					}
				} catch (SQLException e) {
					player.getActionSender().sendMessage(COMMAND_PREFIX + "A MySQL error has occured! " + e.getMessage());
					return;
				}
			} else {
				currentIp = target.getCurrentIP();
			}

			if (currentIp == null) {
				player.getActionSender().sendMessage(COMMAND_PREFIX + "An unknown error has occured!");
				return;
			}

			player.getActionSender().sendMessage(COMMAND_PREFIX + "Fetching characters..");

			try {
				Statement statement = World.getWorld().getDB().getConnection().createStatement();
				ResultSet result = statement.executeQuery("SELECT * FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` WHERE `login_ip` LIKE '%" + currentIp + "%'");

				List<String> names = new ArrayList<>();

				while (result.next()) {
					names.add(result.getString("username"));
				}

				StringBuilder builder = new StringBuilder("@red@").append(args[0].toUpperCase()).append(" @whi@currently has ").append(names.size() > 0 ? "@gre@" : "@red@").append(names.size()).append(" @whi@registered characters.");

				if (names.size() > 0) {
					builder.append(" % % They are: ");
				}

				for (int i = 0; i < names.size(); i++) {
					builder.append("@yel@").append(names.get(i));

					if (i != names.size() - 1) {
						builder.append("@whi@, ");
					}
				}

				player.getActionSender().sendAlert(builder.toString(), names.size() > 10);
			} catch (SQLException e) {
				player.getActionSender().sendMessage(COMMAND_PREFIX + "A MySQL error has occured! " + e.getMessage());
			}
		} else if(command.equals("item")) { //DEV ONLY
			int item = Integer.parseInt(args[0]);
			int amt = Integer.parseInt(args[1]);
			player.getInventory().add(new InvItem(item, amt));
			player.getActionSender().sendInventory();
		} else if(command.equals("npc")) {
			int npcId = Integer.parseInt(args[0]);
			final Npc n = new Npc(npcId, player.getX() + 1, player.getY() + 1, player.getX() - 5, 
					player.getX() + 5, player.getY() - 5, player.getY() + 5);
			n.setRespawn(false);
			World.getWorld().registerNpc(n);
			World.getWorld().getDelayedEventHandler().add(new SingleEvent(null, 60000) {
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
		} else if(command.equals("object")) {
		    if (args.length < 1 || args.length > 3) {
		    	player.getActionSender().sendMessage("Invalid args. Syntax: OBJECT id [direction] (store in db) true/false");
		    	return;
		    }
		    boolean percist = (args.length > 1 ? args[2].equalsIgnoreCase("true") : false);
		    int id = Integer.parseInt(args[0]);
		    if (id < 0) {
		    	GameObject object = world.getTile(player.getLocation()).getGameObject();
			    if (object != null) {
				    world.unregisterGameObject(object);
				    if(percist) {
				    	player.getActionSender().sendMessage("Deleted object from the database");
				    	world.getDB().deleteGameObjectFromDatabase(object);
				    }
				}
		    }
		    else if (EntityHandler.getGameObjectDef(id) != null) {
		    	int dir = args.length == 2 ? Integer.parseInt(args[1]) : 0;
		    	GameObject obj = new GameObject(player.getLocation(), id, dir, 0);
		    	world.registerGameObject(obj);
		    	if(percist) {
		    		player.getActionSender().sendMessage("Stored object in the database");
		    		world.getDB().storeGameObjectToDatabase(obj);
		    	}
		    } 
		    else {
		    	player.getActionSender().sendMessage("Invalid id");
		    }
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
