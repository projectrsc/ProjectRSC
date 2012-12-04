package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface PlayerAttackNpcListener {
	
	 public void onPlayerAttackNpc(Player p, Npc affectedmob);

}
