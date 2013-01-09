package com.prsc.gs.plugins.misc;


import com.prsc.config.Formulae;
import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.listeners.action.InvUseOnNpcListener;
import com.prsc.gs.plugins.listeners.action.InvUseOnObjectListener;
import com.prsc.gs.plugins.listeners.action.ObjectActionListener;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;

/**
 * Theoretically we do not need to block, as everything that is not
 * handled should be handled here.
 * @author openfrog
 *
 */

public class Default implements TalkToNpcListener, ObjectActionListener, InvUseOnObjectListener, InvUseOnNpcListener {
	
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		p.getActionSender().sendMessage("The " + n.getDef().getName() + " does not appear interested in talking");
	}
	
	@Override
    public void onInvUseOnObject(GameObject object, InvItem item, Player owner) {
		//owner.getActionSender().sendMessage("Nothing interesting happens"); UNCOMMENT WHEN EVERYTHING IS PY PLUGIN
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, InvItem item) {
		player.getActionSender().sendMessage("Nothing interesting happens");
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		
	}
}
