package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Item;
import com.prsc.gs.model.Player;

public interface PickupListener {
    /**
     * Called when a user picks up an item
     */
    public void onPickup(Player p, Item i);
}
