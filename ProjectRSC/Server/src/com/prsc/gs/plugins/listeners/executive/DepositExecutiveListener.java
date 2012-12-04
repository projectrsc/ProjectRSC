package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Player;

public interface DepositExecutiveListener {
    /**
     * Return true if you wish to prevent a user from depositing an item
     */
    public boolean blockDeposit(Player p, int itemID, int amount);
}
