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

	public static final World world = World.getWorld();

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
			}
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
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " took " + affectedPlayer.getUsername() + " from " + affectedPlayer.getLocation().toString() + " to admin room"));

			affectedPlayer.teleport(78, 1642, true);

			if (command.equals("take")) {
				player.teleport(76, 1642, true);
			}
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
			//Services.lookup(DatabaseManager.class).addQuery(new StaffLog(player.getUsername() + " kicked " + p.getUsername()));
		}
	}

}
