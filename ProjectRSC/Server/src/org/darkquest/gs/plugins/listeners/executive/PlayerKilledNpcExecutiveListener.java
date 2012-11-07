package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;

public interface PlayerKilledNpcExecutiveListener {
    /**
     * Return true to prevent the default action on a npcs death (no loot)
     */
    public boolean blockPlayerKilledNpc(Player p, Npc n);
}
