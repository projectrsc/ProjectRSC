package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

public interface DropExecutiveListener {
    /**
     * Return true if you wish to prevent a user from dropping an item
     */
    public boolean blockDrop(Player p, InvItem i);
}
