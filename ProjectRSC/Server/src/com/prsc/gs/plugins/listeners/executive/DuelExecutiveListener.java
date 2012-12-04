package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Player;

public interface DuelExecutiveListener {
    /**
     * Return true if you wish to prevent a user from duelling
     */
    public boolean blockDuel(Player p, Player p2);
}
