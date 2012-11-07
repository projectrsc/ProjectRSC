package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

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
