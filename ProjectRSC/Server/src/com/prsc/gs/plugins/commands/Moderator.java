package com.prsc.gs.plugins.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.prsc.config.Constants;
import com.prsc.gs.db.DatabaseManager;
import com.prsc.gs.db.query.StaffLog;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Point;
import com.prsc.gs.plugins.listeners.action.CommandListener;
import com.prsc.gs.service.Services;
import com.prsc.gs.tools.DataConversions;
import com.prsc.gs.world.World;

public final class Moderator implements CommandListener {

	public static final World world = World.getWorld();
	
	private static final String[] towns = { "varrock", "falador", "draynor", "portsarim", "karamja", "alkharid", "lumbridge", "edgeville", "castle", "taverly", "clubhouse", "seers", "barbarian", "rimmington", "catherby", "ardougne", "yanille", "lostcity", "gnome" };

	private static final Point[] townLocations = { Point.location(122, 509), Point.location(304, 542), Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693), Point.location(120, 648), Point.location(217, 449), Point.location(270, 352), Point.location(373, 498), Point.location(653, 491), Point.location(501, 450), Point.location(233, 513), Point.location(325, 663), Point.location(440, 501), Point.location(549, 589), Point.location(583, 747), Point.location(127, 3518), Point.location(703, 527) };

	private void sendInvalidArguments(Player p, String... strings) {
		StringBuilder sb = new StringBuilder(COMMAND_PREFIX + "Invalid arguments @red@Syntax: @whi@");

		for (int i = 0; i < strings.length; i++) {
			sb.append(i == 0 ? strings[i].toUpperCase() : strings[i]).append(i == (strings.length - 1) ? "" : " ");
		}
		p.getActionSender().sendMessage(sb.toString());
	}

	private static final String COMMAND_PREFIX = "@red@SERVER: @whi@";

	@Override
	public void onCommand(String command, String[] args, Player player) {
		if (!player.isMod()) {
			return;
		}
		if (command.equals("mute") || command.equals("unmute")) {
			boolean mute = command.equals("mute");

			if (args.length != 1) {
				sendInvalidArguments(player, mute ? "mute" : "unmute", "name reason");
				return;
			}
			Player affectedPlayer = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if (affectedPlayer == null) {
				player.getActionSender().sendMessage(COMMAND_PREFIX + "Invalid player");
				return;
			}

			affectedPlayer.setMuteTime(mute ? -1 : 0);
			player.getActionSender().sendMessage(COMMAND_PREFIX + args[0] + " has been " + (mute ? "muted" : "unmuted"));
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player, (mute ? 0 : 1), affectedPlayer));
		} else if (command.equals("goto") || command.equals("summon")) {
			boolean summon = command.equals("summon");

			if (args.length != 1) {
				sendInvalidArguments(player, summon ? "summon" : "goto", "name");
				return;
			}
			long usernameHash = DataConversions.usernameToHash(args[0]);
			Player affectedPlayer = world.getPlayer(usernameHash);

			if (affectedPlayer != null) {
				if (summon) {
					//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " summoned " + affectedPlayer.getUsername() + " from " + affectedPlayer.getLocation().toString() + " to " + player.getLocation().toString()));
					affectedPlayer.teleport(player.getX(), player.getY(), true);
				} else {
					//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " went from " + player.getLocation() + " to " + affectedPlayer.getUsername() + " at " + affectedPlayer.getLocation().toString()));
					if(!player.isAdmin() && Point.inWilderness(affectedPlayer.getX(), affectedPlayer.getY())) {
						player.getActionSender().sendMessage("Mods cannot teleport in the wilderness");
					} else {
						player.teleport(affectedPlayer.getX(), affectedPlayer.getY(), true);
					}
				}
			} else {
				player.getActionSender().sendMessage(COMMAND_PREFIX + "Invalid player");
				return;
			}
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player, (summon ? 2 : 3), affectedPlayer));
		} else if (command.equals("take") || command.equals("put")) {
			boolean take = command.equals("take");
			if (args.length != 1) {
				player.getActionSender().sendMessage("Invalid args. Syntax: TAKE name");
				return;
			}
			Player affectedPlayer = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (affectedPlayer == null) {
				player.getActionSender().sendMessage("Invalid player, maybe they aren't currently online?");
				return;
			}
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " took " + affectedPlayer.getUsername() + " from " + affectedPlayer.getLocation().toString() + " to admin room"));

			affectedPlayer.teleport(78, 1642, true);

			if (take) {
				player.teleport(76, 1642, true);
			}
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player, (take ? 4 : 5), affectedPlayer));
		} else if (command.equals("info")) {
			if (args.length != 1) {
				sendInvalidArguments(player, "info", "name");
				return;
			}
			World.getWorld().getServer().getLoginConnector().getActionSender().requestPlayerInfo(player, DataConversions.usernameToHash(args[0]));
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " requested info for " + args[0]));
		} else if (command.equalsIgnoreCase("kick")) {
			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				return;
			}
			p.destroy(false);
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player, 6, p));
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " kicked " + p.getUsername()));
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
		} else if (command.equals("invis")) {
			if (player.isInvis()) {
				player.setinvis(false);
			} else {
				player.setinvis(true);
			}
			player.getActionSender().sendMessage(COMMAND_PREFIX + "You are now " + (player.isInvis() ? "invisible" : "visible"));
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " went " + (player.isInvis() ? "in" : "") + "visible"));
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
		}
	}

}
