package com.prsc.gs.plugins.misc;

import com.prsc.gs.model.ChatMessage;

import com.prsc.gs.model.Item;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.listeners.action.PickupListener;
import com.prsc.gs.plugins.listeners.executive.PickupExecutiveListener;
import com.prsc.gs.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.prsc.gs.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import com.prsc.gs.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;

/**
 * 
 * @author xEnt
 *
 */
public class Zamorak implements PickupListener, PickupExecutiveListener, PlayerAttackNpcExecutiveListener, PlayerRangeNpcExecutiveListener, PlayerMageNpcExecutiveListener {

	@Override
	public void onPickup(Player owner, Item item) {
		if (item.getID() == 501 && item.getX() == 333 && item.getY() == 434) {
			Npc zam = World.getWorld().getNpc(140, 328, 333, 433, 438, true);
			if (zam != null && !zam.inCombat()) {
				applyCurse(owner, zam);
				zam.attack(owner);
				return;
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, Item i) {
		if(i.getID() == 501) {
			Npc zam = World.getWorld().getNpc(140, 328, 333, 433, 438, true);
			if(zam == null || zam.inCombat())
				return false;
			else 
				return true;
		}
		return false;
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if(n.getID() == 140)
			applyCurse(p, n);
		return false;
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == 140)
			applyCurse(p, n);
		return false;
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		if(n.getID() == 140)
			applyCurse(p, n);
		return false;
	}

	public void applyCurse(Player owner, Npc zam) {
		owner.informOfNpcMessage(new ChatMessage(zam, "a curse be upon you", owner));
		for (int i = 0; i < 3; i++) {
			int stat = owner.getCurStat(i);
			if (stat < 3)
				owner.setCurStat(i, 0);
			else
				owner.setCurStat(i, stat - 3);
		}
		owner.getActionSender().sendStats();
	}
}
