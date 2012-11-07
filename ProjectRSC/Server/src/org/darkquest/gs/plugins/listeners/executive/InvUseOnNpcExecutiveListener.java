package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;

public interface InvUseOnNpcExecutiveListener {
	
	public boolean blockInvUseOnNpc(Player player, Npc npc, InvItem item);

}
