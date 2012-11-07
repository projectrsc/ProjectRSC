package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Player;

public interface PlayerDeathListener {
    /**
     * Called on a players death
     *
     * @param p
     */
    public void onPlayerDeath(Player p);

}
