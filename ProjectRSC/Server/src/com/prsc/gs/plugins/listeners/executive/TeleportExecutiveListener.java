package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Player;

public interface TeleportExecutiveListener {
    /**
     * Return true to prevent teleportation (this includes ::stuck)
     * This does not include teleportations without bubbles (stairs, death, ladders etc)
     *
     * @return
     */
    public boolean blockTeleport(Player p);
}
