package org.darkquest.gs.plugins.misc;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;

/**
 * Theoretically we do not need to block, as everything that is not
 * handled should be handled here.
 * @author openfrog
 *
 */

public class Default implements TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		p.getActionSender().sendMessage("The " + n.getDef().getName() + " does not appear interested in talking");
	}
}
