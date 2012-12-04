package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface PlayerAttackNpcExecutiveListener {
	
	public boolean blockPlayerAttackNpc(Player p, Npc n);

}
