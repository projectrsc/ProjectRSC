package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface InvUseOnNpcListener {
	
	public void onInvUseOnNpc(Player player, Npc npc, InvItem item);

}
