package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

public interface DropExecutiveListener {
    /**
     * Return true if you wish to prevent a user from dropping an item
     */
    public boolean blockDrop(Player p, InvItem i);
}
