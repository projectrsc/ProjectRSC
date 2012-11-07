package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Player;

public interface DepositExecutiveListener {
    /**
     * Return true if you wish to prevent a user from depositing an item
     */
    public boolean blockDeposit(Player p, int itemID, int amount);
}
