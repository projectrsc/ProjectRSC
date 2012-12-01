package org.darkquest.gs.plugins.commands;

import org.darkquest.config.Constants;

import org.darkquest.gs.connection.filter.ConnectionFilter;
import org.darkquest.gs.db.DatabaseManager;
import org.darkquest.gs.db.query.StaffLog;
import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.event.SingleEvent;
import org.darkquest.gs.external.EntityHandler;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Mob;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.plugins.listeners.action.CommandListener;
import org.darkquest.gs.service.Services;
import org.darkquest.gs.states.CombatState;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.World;

public final class Admins implements CommandListener {

	private final World world = World.getWorld();

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
				World.getWorld().getServer().getLoginConnector().getActionSender().saveProfiles(true);
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
		} else if (command.equals("blink")) {
			player.setBlink(!player.blink());
			player.getActionSender().sendMessage(COMMAND_PREFIX + "Your blink status is now " + player.blink());
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " changed blink status to " + player.blink()));
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
		} else if(command.equals("setglobalexp")) {
			if (args.length != 2) {
				player.getActionSender().sendMessage("Invalid args. Syntax: setglobalexp rate(double) lengthoftime(minutes)");
				return;
			}
			final double curExpRate = Constants.GameServer.EXP_RATE;
			double changedRate = Double.parseDouble(args[0]);
			int time = Integer.parseInt(args[1]) * 60000;
			for(Player p : World.getWorld().getPlayers()) {
				p.getActionSender().sendMessage("@cya@Global exp rate has changed to @yel@x" + changedRate);
				p.getActionSender().sendMessage("@cya@This rate will last for @yel@" + Integer.parseInt(args[1]) + " minutes");
			}
			Constants.GameServer.EXP_RATE = changedRate;
			world.getDelayedEventHandler().add(new DelayedEvent(null, time) {
				
				@Override
				public void run() {
					Constants.GameServer.EXP_RATE = curExpRate;
					for(Player p : World.getWorld().getPlayers()) {
						p.getActionSender().sendMessage("@cya@Global exp rate has changed back to @yel@x" + curExpRate);
					}
				}
				
			});
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
