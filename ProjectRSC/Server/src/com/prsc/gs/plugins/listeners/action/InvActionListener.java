package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

/**
 * Interface for handling Inv Actions
 *
 * @author Peeter.tomberg
 */
public interface InvActionListener {

    /**
     * Called when a user performs an inventory action
     *
     * @param item
     * @param player
     */
    public void onInvAction(InvItem item, Player player);
}
