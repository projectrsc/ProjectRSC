package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface PlayerKilledNpcExecutiveListener {
    /**
     * Return true to prevent the default action on a npcs death (no loot)
     */
    public boolean blockPlayerKilledNpc(Player p, Npc n);
}
