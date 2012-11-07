package org.darkquest.gs.plugins.misc;

import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import org.darkquest.gs.world.World;

public class Bed implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(GameObject object, String command, Player owner) {	
		if(command.equalsIgnoreCase("rest")) {
			World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
			    public void action() {
			    	owner.getActionSender().sendEnterSleep();
			    	owner.startSleepEvent(true);
			    }
			});
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return command.equalsIgnoreCase("rest");
	}
}
