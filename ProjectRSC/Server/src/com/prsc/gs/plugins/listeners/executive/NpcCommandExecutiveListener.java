package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface NpcCommandExecutiveListener {
	
	public boolean blockNpcCommand(Npc n, Player p);

}
