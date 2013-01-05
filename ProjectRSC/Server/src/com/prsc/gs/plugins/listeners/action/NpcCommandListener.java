package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface NpcCommandListener {

	public void onNpcCommand(Npc n, Player p);
	
}
