package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.Player;

public interface ObjectActionExecutiveListener {

    /**
     * Prevent a user from activating an in-game object.
     */
    public boolean blockObjectAction(GameObject obj, String command, Player player);
}
