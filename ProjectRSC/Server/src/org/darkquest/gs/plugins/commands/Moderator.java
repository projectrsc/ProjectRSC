package org.darkquest.gs.plugins.commands;

import org.darkquest.gs.db.DatabaseManager;
import org.darkquest.gs.db.query.StaffLog;
import org.darkquest.gs.model.Player;
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
		} 
	}

}
