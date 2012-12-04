package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

/**
 * Interface for handling player log outs
 */
public interface PlayerLogoutListener {
    /**
     * Called when player logs out (by himself, or when he's logged out by a timeout, mod etc)
     */
    public void onPlayerLogout(Player player);
}
