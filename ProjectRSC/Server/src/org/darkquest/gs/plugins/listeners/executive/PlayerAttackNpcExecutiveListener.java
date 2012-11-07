package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;

public interface PlayerAttackNpcExecutiveListener {
	
	public boolean blockPlayerAttackNpc(Player p, Npc n);

}
