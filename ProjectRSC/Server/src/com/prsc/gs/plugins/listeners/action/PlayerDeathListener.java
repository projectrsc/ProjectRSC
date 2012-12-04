package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

public interface PlayerDeathListener {
    /**
     * Called on a players death
     *
     * @param p
     */
    public void onPlayerDeath(Player p);

}
