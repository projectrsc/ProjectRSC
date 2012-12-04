package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.Player;

public interface ObjectActionListener {

    /**
     * When a user activates an in-game Object.
     */
    public void onObjectAction(GameObject obj, String command, Player player);

}
