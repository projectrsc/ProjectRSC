package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;

public interface InvUseOnNpcListener {
	
	public void onInvUseOnNpc(Player player, Npc npc, InvItem item);

}
