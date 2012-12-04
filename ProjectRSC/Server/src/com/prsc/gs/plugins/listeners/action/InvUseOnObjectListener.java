package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

public interface InvUseOnObjectListener {

    /**
     * Called when a user uses an inventory item on an game object
     */
    public void onInvUseOnObject(GameObject obj, InvItem item, Player player);
}
