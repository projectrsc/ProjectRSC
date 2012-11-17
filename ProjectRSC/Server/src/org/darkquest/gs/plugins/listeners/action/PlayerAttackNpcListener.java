package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;

public interface PlayerAttackNpcListener {
	
	 public void onPlayerAttackNpc(Player p, Npc affectedmob);

}
