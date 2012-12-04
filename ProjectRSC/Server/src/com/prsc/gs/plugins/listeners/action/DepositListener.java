package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

public interface DepositListener {
    /**
     * Called when a user deposits an item
     */
    public void onDeposit(Player p, int itemID, int amount);
}
