package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

public interface InvUseOnObjectExecutiveListener {
    /**
     * Return true to prevent a user when he uses an inventory item on an game object
     */
    public boolean blockInvUseOnObject(GameObject obj, InvItem item, Player player);
}
