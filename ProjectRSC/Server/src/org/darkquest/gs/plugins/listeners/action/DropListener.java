package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

public interface DropListener {
    /**
     * Called when a user drops an item
     */
    public void onDrop(Player p, InvItem i);
}
