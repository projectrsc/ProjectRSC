package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.action.InvUseOnObjectListener;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class DruidicRitual extends Quest implements TalkToNpcExecutiveListener, TalkToNpcListener,
													InvUseOnObjectExecutiveListener, InvUseOnObjectListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.DRUIDIC_RITUAL;
	}

	@Override
	public String getQuestName() {
		return "Druidic ritual (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {

	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return false;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, InvItem item, Player player) {
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {

	}

	@Override
	public void onInvUseOnObject(GameObject obj, InvItem item, Player player) {

	}
}