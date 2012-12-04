package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

public interface DropListener {
    /**
     * Called when a user drops an item
     */
    public void onDrop(Player p, InvItem i);
}
