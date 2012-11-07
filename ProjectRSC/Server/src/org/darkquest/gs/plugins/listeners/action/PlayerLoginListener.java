package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Player;

/**
 * Interface for handling player logins
 */
public interface PlayerLoginListener {
    /**
     * Called when player logins
     */
    public void onPlayerLogin(Player player);
}
