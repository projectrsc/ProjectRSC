package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Player;

public interface PlayerRangeExecutiveListener {

    /**
     * Return true if you wish to prevent a user from ranging a player
     */
    public boolean blockPlayerRange(Player p, Player affectedMob);
}
