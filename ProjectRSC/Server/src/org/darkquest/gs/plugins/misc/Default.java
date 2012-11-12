package org.darkquest.gs.plugins.misc;

import org.darkquest.config.Formulae;

import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.InvUseOnNpcListener;
import org.darkquest.gs.plugins.listeners.action.InvUseOnObjectListener;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;

/**
 * Theoretically we do not need to block, as everything that is not
 * handled should be handled here.
 * @author openfrog
 *
 */

public class Default implements TalkToNpcListener, InvUseOnObjectListener, InvUseOnNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		p.getActionSender().sendMessage("The " + n.getDef().getName() + " does not appear interested in talking");
		int dir = Formulae.getDirection(p, n);
		if (dir != -1) {
			n.setSprite(n.getSprite());	// change?
		}
	}
	
	@Override
    public void onInvUseOnObject(GameObject object, InvItem item, Player owner) {
		owner.getActionSender().sendMessage("Nothing interesting happens");
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, InvItem item) {
		player.getActionSender().sendMessage("Nothing interesting happens");
	}
}
