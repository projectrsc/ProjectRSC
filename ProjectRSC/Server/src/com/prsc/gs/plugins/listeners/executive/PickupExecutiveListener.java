package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Item;
import com.prsc.gs.model.Player;

public interface PickupExecutiveListener {
    /**
     * Return true if you wish to prevent a user from picking up an item
     */
    public boolean blockPickup(Player p, Item i);
}
