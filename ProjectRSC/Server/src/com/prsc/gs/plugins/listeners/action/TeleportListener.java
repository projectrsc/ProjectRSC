package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

public interface TeleportListener {

    /**
     * Called when a user teleports (includes ::stuck)
     * This does not include teleportations without bubbles (stairs, death, ladders etc)
     */
    public void onTeleport(Player p);
}
