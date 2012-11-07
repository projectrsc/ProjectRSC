package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Player;

public interface DepositListener {
    /**
     * Called when a user deposits an item
     */
    public void onDeposit(Player p, int itemID, int amount);
}
