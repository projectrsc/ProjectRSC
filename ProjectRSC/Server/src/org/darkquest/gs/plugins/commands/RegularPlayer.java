package org.darkquest.gs.plugins.commands;

import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.CommandListener;
import org.darkquest.gs.world.World;

public final class RegularPlayer implements CommandListener {

	@Override
	public void onCommand(String command, String[] args, Player player) {
		if(command.equals("online")) {
			player.getActionSender().sendMessage("Online players: " + World.getWorld().getPlayers().size());
			return;
		}		
	}
}
