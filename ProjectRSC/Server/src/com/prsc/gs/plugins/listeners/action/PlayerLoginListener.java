package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

/**
 * Interface for handling player logins
 */
public interface PlayerLoginListener {
    /**
     * Called when player logins
     */
    public void onPlayerLogin(Player player);
}
