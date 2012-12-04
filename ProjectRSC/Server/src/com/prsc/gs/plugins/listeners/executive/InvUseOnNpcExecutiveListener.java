package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface InvUseOnNpcExecutiveListener {
	
	public boolean blockInvUseOnNpc(Player player, Npc npc, InvItem item);

}
