package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Player;

public interface PlayerAttackExecutiveListener {
    /**
     * Return true if you wish to prevent a user from attacking a mob
     */
    public boolean blockPlayerAttack(Player p, Player affectedmob);
}
