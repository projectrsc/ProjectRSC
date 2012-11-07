package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.Player;

public interface WallObjectActionExecutiveListener {

    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player);

}
