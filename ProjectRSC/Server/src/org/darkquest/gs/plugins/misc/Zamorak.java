package org.darkquest.gs.plugins.misc;

import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.PickupListener;
import org.darkquest.gs.plugins.listeners.executive.PickupExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.PlayerMageNpcExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.PlayerRangeNpcExecutiveListener;
import org.darkquest.gs.world.World;

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
			else return true;
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
