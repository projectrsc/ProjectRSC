package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.Player;

public interface WallObjectActionListener {
	
	public void onWallObjectAction(GameObject obj, Integer click, Player p);
	
}
