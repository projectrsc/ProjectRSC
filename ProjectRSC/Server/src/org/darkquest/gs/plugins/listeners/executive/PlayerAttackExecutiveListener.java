package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Player;

public interface PlayerAttackExecutiveListener {
    /**
     * Return true if you wish to prevent a user from attacking a mob
     */
    public boolean blockPlayerAttack(Player p, Player affectedmob);
}
