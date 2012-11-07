package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Player;

public interface CommandListener {
    public void onCommand(String command, String[] args, Player player);
}
