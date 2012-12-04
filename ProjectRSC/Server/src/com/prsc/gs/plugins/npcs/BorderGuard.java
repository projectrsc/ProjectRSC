package com.prsc.gs.plugins.npcs;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Point;
import com.prsc.gs.plugins.ScriptablePlug;
import com.prsc.gs.plugins.listeners.action.ObjectActionListener;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class BorderGuard extends ScriptablePlug implements TalkToNpcExecutiveListener, TalkToNpcListener, ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		//if(n.getID() != 161)
		//	return;
		p.setBusy(true);
		n.blockedBy(p);

		playMessages(p, n, true, "Can I come through this gate?");
		playMessages(p, n, false, "You must pay a toll of 10 gold coins to pass");

		String[] options = new String[] { "No thankyou, I'll walk round", "Who does my money go to?", "yes ok" };

		p.setMenuHandler(new MenuHandler(options) {
			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, n, true, reply);

				switch(option) {
				case 0: // no thanks
					playMessages(owner, n, false, "Ok suit yourself");

					n.unblock();
					break;
				case 1: // who does money go to
					playMessages(owner, n, false, "The money goes to the city of Al Kharid");

					n.unblock();
					break;
				case 2: // yes
					if(owner.getInventory().remove(10, 10) > -1) { // enough money
						owner.getActionSender().sendInventory();
						owner.getActionSender().sendMessage("You pay the guard");
						playMessages(owner, n, false, "You may pass");
						owner.getActionSender().sendMessage("The gate swings open"); // lold
						//x > 91 (left side of fence
						if(owner.getX() > 91)
							owner.teleport(90, 649, false);
						else
							owner.teleport(93, 649, false);
					

						n.unblock();
					} else { // not enough money
						playMessages(owner, n, true, "Oh dear I don't actually seem to have enough money");

						n.unblock();
					}
					break;
				}

				owner.setBusy(false);
			}
		});
		p.getActionSender().sendMenu(options);
		p.setBusy(false);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 161;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 180 && command.equals("open")) {
			player.getActionSender().sendMessage("You need to talk to the border guard");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 180 && command.equals("open"); 
	}
}
