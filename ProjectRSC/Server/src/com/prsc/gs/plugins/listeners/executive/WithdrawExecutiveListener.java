package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Player;

public interface WithdrawExecutiveListener {
    /**
     * Return true if you wish to prevent a user from withdrawing an item
     */
    public void blockWithdraw(Player p, int itemID, int amount);
}
