package org.darkquest.gs.plugins.misc;

import java.util.Arrays;

import org.darkquest.gs.event.MiniEvent;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.ObjectActionListener;
import org.darkquest.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import org.darkquest.gs.world.World;

public class Dummy implements ObjectActionExecutiveListener, ObjectActionListener {

	static int[] ids;
	static {
		ids = new int[] { 49 };
		Arrays.sort(ids);
	}
	
	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return Arrays.binarySearch(ids, obj.getID()) >= 0;
	}

	@Override
	public void onObjectAction(GameObject object, String command, Player owner) {	
		if(Arrays.binarySearch(ids, object.getID()) >= 0) {
			owner.setBusy(true);
			owner.getActionSender().sendMessage("You swing at the dummy");

			World.getWorld().getDelayedEventHandler().add(new MiniEvent(owner, 3500) {
				public void action() {
					owner.setBusy(false);
					if (owner.getCurStat(0) > 7) {
						owner.getActionSender().sendMessage("There is only so much you can learn from hitting a dummy");
						return;
					}
					owner.getActionSender().sendMessage("You hit the dummy");
					owner.incExp(0, 5, true);
					owner.getActionSender().sendStat(0);
				}
			});
			return;
		}
	}

}
