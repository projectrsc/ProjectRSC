package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

public interface InvUseOnObjectListener {

    /**
     * Called when a user uses an inventory item on an game object
     */
    public void onInvUseOnObject(GameObject obj, InvItem item, Player player);
}
