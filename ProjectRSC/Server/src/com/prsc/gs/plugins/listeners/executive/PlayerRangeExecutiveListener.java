package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Player;

public interface PlayerRangeExecutiveListener {

    /**
     * Return true if you wish to prevent a user from ranging a player
     */
    public boolean blockPlayerRange(Player p, Player affectedMob);
}
