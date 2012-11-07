package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.Player;

public interface WallObjectActionListener {
	
	public void onWallObjectAction(GameObject obj, Integer click, Player p);
	
}
