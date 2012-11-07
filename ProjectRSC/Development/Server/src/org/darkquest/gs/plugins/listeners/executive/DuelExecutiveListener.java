package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Player;

public interface DuelExecutiveListener {
    /**
     * Return true if you wish to prevent a user from duelling
     */
    public boolean blockDuel(Player p, Player p2);
}
