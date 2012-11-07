package org.darkquest.gs.plugins.misc;

import org.darkquest.gs.event.MiniEvent;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import org.darkquest.gs.world.World;

public class Hopper implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(GameObject object, String command, Player owner) {	
		if(command.equalsIgnoreCase("operate")) {
			if(object.containsItem() != 29) {
				owner.getActionSender().sendMessage("The hopper is empty...");
				return;
			}
			owner.getActionSender().sendMessage("You operate the hopper..");
			
			World.getWorld().getDelayedEventHandler().add(new MiniEvent(owner, 1000) {
				public void action() {
					owner.getActionSender().sendMessage("The grain slides down the chute");
				}
			});
			
			if (object.getX() == 179 && object.getY() == 2371) {
				World.getWorld().registerItem(new Item(23, 179, 481, 1, owner));
			} else {
				World.getWorld().registerItem(new Item(23, 166, 599, 1, owner));
			}
			object.containsItem(-1);
			return;
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return command.equalsIgnoreCase("operate");
	}
}
