package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface PlayerRangeNpcExecutiveListener {

    /**
     * Return true if you wish to prevent a user from ranging a player
     */
    public boolean blockPlayerRangeNpc(Player p, Npc n);
}
