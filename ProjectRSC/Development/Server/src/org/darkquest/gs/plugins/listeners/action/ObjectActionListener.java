package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.Player;

public interface ObjectActionListener {

    /**
     * When a user activates an in-game Object.
     */
    public void onObjectAction(GameObject obj, String command, Player player);

}
