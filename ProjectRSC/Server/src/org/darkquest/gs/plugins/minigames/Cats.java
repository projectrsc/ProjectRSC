package org.darkquest.gs.plugins.minigames;

import org.darkquest.config.Constants;
import org.darkquest.gs.event.SingleEvent;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.DropListener;
import org.darkquest.gs.plugins.listeners.executive.DropExecutiveListener;
import org.darkquest.gs.world.World;

public final class Cats implements DropExecutiveListener, DropListener {

	/**
	 * TODO: finish this
	 */
	@Override
	public boolean blockDrop(Player p, final InvItem i) {
		if (!Constants.GameServer.MEMBER_WORLD || i.getID() != 1096) {
			return false;
		}
		return true;
	}

	@Override
	public void onDrop(Player p, final InvItem i) {
		if (!Constants.GameServer.MEMBER_WORLD || i.getID() != 1096) {
			return;
		}
		
		p.getActionSender().sendMessage("you drop the kitten");
		
		World.getWorld().getDelayedEventHandler().add(new SingleEvent(p, 1500) {

			@Override
			public void action() {
				owner.getActionSender().sendMessage("it gets upset and runs away");
				
				owner.getInventory().remove(i);
				owner.getActionSender().sendInventory();
			}
			
		});
	}

}
