package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.Player;

public interface WallObjectActionExecutiveListener {

    public boolean blockWallObjectAction(GameObject obj, Integer click, Player player);

}
