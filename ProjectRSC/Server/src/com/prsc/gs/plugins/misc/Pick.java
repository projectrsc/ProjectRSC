package com.prsc.gs.plugins.misc;

import com.prsc.gs.event.SingleEvent;
import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.listeners.action.ObjectActionListener;
import com.prsc.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.prsc.gs.world.World;

public final class Pick implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(GameObject object, String command, Player owner) {	
		switch (object.getID()) {
		case 72: // Wheat
			owner.getActionSender().sendMessage("You get some grain");
			owner.getInventory().add(new InvItem(29, 1));
			break;
		case 191: // Potatos
			owner.getActionSender().sendMessage("You pick a potato");
			owner.getInventory().add(new InvItem(348, 1));
			break;
		case 313: // Flax
			handleFlaxPickup(owner);
			break;
		case 183: // Banana
			owner.getActionSender().sendMessage("You pull a banana off the tree");
			owner.getInventory().add(new InvItem(249, 1));
			break;
		default:
			owner.getActionSender().sendMessage("Nothing interesting happens.");
			return;
		}

		owner.getActionSender().sendInventory();
		owner.getActionSender().sendSound("potato");
		owner.setBusy(true);
		World.getWorld().getDelayedEventHandler().add(new SingleEvent(owner, 200) {
			public void action() {
				owner.setBusy(false);
			}
		});
		return;
	}

	private void handleFlaxPickup(Player owner) {
		owner.setBusy(true);
		owner.getActionSender().sendMessage("You uproot a flax plant");
		owner.getInventory().add(new InvItem(675, 1));
		owner.getActionSender().sendInventory();
		if(!owner.getInventory().full()) {
			World.getWorld().getDelayedEventHandler().add(new SingleEvent(owner, 200) {
				public void action() {
					owner.setBusy(false);
					handleFlaxPickup(owner);
				}
			});
		}
		else {
			owner.setBusy(false);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return command.equals("pick") || command.equals("pick banana");
	}
}
