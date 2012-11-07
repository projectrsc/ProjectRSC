package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;

public interface PlayerMageNpcExecutiveListener {

    /**
     * Return true if you wish to prevent a user from ranging a player
     */
    public boolean blockPlayerMageNpc(Player p, Npc n);
}
