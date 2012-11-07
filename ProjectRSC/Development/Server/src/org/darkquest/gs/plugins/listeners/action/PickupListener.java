package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.Player;

public interface PickupListener {
    /**
     * Called when a user picks up an item
     */
    public void onPickup(Player p, Item i);
}
