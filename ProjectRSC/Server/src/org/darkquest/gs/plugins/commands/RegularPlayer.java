package org.darkquest.gs.plugins.commands;

import org.darkquest.gs.model.Player;

import org.darkquest.gs.plugins.listeners.action.CommandListener;

public final class RegularPlayer implements CommandListener {

	@Override
	public void onCommand(String command, String[] args, Player player) {
		if(command.equals("stuck")) {
			if(!player.inCombat())
				player.setBusy(false);
			return;
		}
	}
}
