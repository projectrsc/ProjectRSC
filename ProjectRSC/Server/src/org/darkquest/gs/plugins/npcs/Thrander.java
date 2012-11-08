package org.darkquest.gs.plugins.npcs;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Thrander extends Scriptable implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		//if(n.getID() != 160)
		//	return;
		p.setBusy(true);
		n.blockedBy(p);
		
		playMessages(p, n, false, "Hello i'm thrander the smith", "I'm an expert in armour modification", "Give me your armour designed for men", "and I can convert it into something more comfortable for a woman", "and vice versa");
		p.setBusy(false);
		n.unblock();
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 160;
	}

}
