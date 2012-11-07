package org.darkquest.gs.plugins.commands;

import org.darkquest.gs.db.DatabaseManager;
import org.darkquest.gs.db.query.StaffLog;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.model.Point;
import org.darkquest.gs.plugins.listeners.action.CommandListener;
import org.darkquest.gs.service.Services;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.World;

public final class Moderator implements CommandListener {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();


	private final String[] towns = {"varrock", "falador", "draynor", "portsarim", "karamja", "alkharid", "lumbridge", "edgeville", "castle", "taverly", "clubhouse", "seers", "barbarian", "rimmington", "catherby", "ardougne", "yanille", "lostcity", "gnome"};

	private final Point[] townLocations = {Point.location(122, 509), Point.location(304, 542), Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693), Point.location(120, 648), Point.location(217, 449), Point.location(270, 352), Point.location(373, 498), Point.location(653, 491), Point.location(501, 450), Point.location(233, 513), Point.location(325, 663), Point.location(440, 501), Point.location(549, 589), Point.location(583, 747), Point.location(127, 3518), Point.location(703, 527)};

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
				sendInvalidArguments(player, mute ? "mute" : "unmute", "name");
				return;
			}
			Player affectedPlayer = world.getPlayer(DataConversions.usernameToHash(args[0]));

			if (affectedPlayer == null) {
				player.getActionSender().sendMessage(COMMAND_PREFIX + "Invalid player");
				return;
			}

			affectedPlayer.setMuteTime(mute ? -1 : 0);
			player.getActionSender().sendMessage(COMMAND_PREFIX + args[0] + " has been " + (mute ? "muted" : "unmuted"));
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " " + (mute ? "muted" : "unmuted") + " " + affectedPlayer.getUsername()));
		} else if (command.equals("ban") || command.equals("unban")) {
			boolean banned = command.equals("ban");
			if (args.length != 1) {
				sendInvalidArguments(player, banned ? "ban" : "unban", "name");
				return;
			}
			world.getServer().getLoginConnector().getActionSender().banPlayer(player, DataConversions.usernameToHash(args[0]), banned);
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " attempted to " + (banned ? "banned" : "unbanned") + " " + args[0]));
		} else if (command.equals("info")) {
			if (args.length != 1) {
				sendInvalidArguments(player, "info", "name");
				return;
			}
			World.getWorld().getServer().getLoginConnector().getActionSender().requestPlayerInfo(player, DataConversions.usernameToHash(args[0]));
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " requested info for " + args[0]));
			return;
		} else if (command.equalsIgnoreCase("town")) {
			try {
				String town = args[0];
				if (town != null) {
					for (int i = 0; i < towns.length; i++)
						if (town.equalsIgnoreCase(towns[i])) {
							player.teleport(townLocations[i].getX(), townLocations[i].getY(), true);
							Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " went to " + args[0]));
							return;
						}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}  else if (command.equals("goto") || command.equals("summon")) {
			boolean summon = command.equals("summon");

			if (args.length != 1) {
				sendInvalidArguments(player, summon ? "summon" : "goto", "name");
				return;
			}
			long usernameHash = DataConversions.usernameToHash(args[0]);
			Player affectedPlayer = world.getPlayer(usernameHash);

			if (affectedPlayer != null) {
				if (summon) {
					Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " summoned " + affectedPlayer.getUsername() + " from " + affectedPlayer.getLocation().toString() + " to " + player.getLocation().toString()));
					affectedPlayer.teleport(player.getX(), player.getY(), true);
				} else {
					Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " went from " + player.getLocation() + " to " + affectedPlayer.getUsername() + " at " + affectedPlayer.getLocation().toString()));
					player.teleport(affectedPlayer.getX(), affectedPlayer.getY(), true);
				}
			} else {
				player.getActionSender().sendMessage(COMMAND_PREFIX + "Invalid player");
			}
		} else if (command.equals("blink")) {
			player.setBlink(!player.blink());
			player.getActionSender().sendMessage(COMMAND_PREFIX + "Your blink status is now " + player.blink());
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " changed blink status to " + player.blink()));
		} else if (command.equals("invis")) {
			if (player.isInvis()) {
				player.setinvis(false);
			} else {
				player.setinvis(true);
			}
			player.getActionSender().sendMessage(COMMAND_PREFIX + "You are now " + (player.isInvis() ? "invisible" : "visible"));
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " went " + (player.isInvis() ? "in" : "") + "visible"));
		} else if (command.equals("teleport")) {
			if (args.length != 2) {
				player.getActionSender().sendMessage("Invalid args. Syntax: TELEPORT x y");
				return;
			}
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			if (world.withinWorld(x, y)) {
				Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " teleported from " + player.getLocation().toString() + " to (" + x + ", " + y + ")"));
				player.teleport(x, y, true);
			} else {
				player.getActionSender().sendMessage("Invalid coordinates!");
			}
			return;
		} else if (command.equalsIgnoreCase("kick")) {
			Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (p == null) {
				return;
			}
			p.destroy(false);
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " kicked " + p.getUsername()));
		} else if (command.equals("take") || command.equals("put")) {
			if (args.length != 1) {
				player.getActionSender().sendMessage("Invalid args. Syntax: TAKE name");
				return;
			}
			Player affectedPlayer = world.getPlayer(DataConversions.usernameToHash(args[0]));
			if (affectedPlayer == null) {
				player.getActionSender().sendMessage("Invalid player, maybe they aren't currently online?");
				return;
			}
			Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " took " + affectedPlayer.getUsername() + " from " + affectedPlayer.getLocation().toString() + " to admin room"));
		
			affectedPlayer.teleport(78, 1642, true);
			if (command.equals("take")) {
				player.teleport(76, 1642, true);
			}
			return;
		}
		return;
	}

}
