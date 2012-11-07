package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.Player;

public interface ObjectActionExecutiveListener {

    /**
     * Prevent a user from activating an in-game object.
     */
    public boolean blockObjectAction(GameObject obj, String command, Player player);
}
