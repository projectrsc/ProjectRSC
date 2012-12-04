package com.prsc.gs.plugins.npcs;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.ScriptablePlug;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Barmaid extends ScriptablePlug implements TalkToNpcExecutiveListener, TalkToNpcListener {
	private final String notEnoughMoney = "Oh dear. I don't seem to have enough money";

	@Override
	public void onTalkToNpc(Player p, final Npc n) { 
		//if(n.getID() != 142)
		//	return;
		p.setBusy(true);
		n.blockedBy(p);

		playMessages(p, n, true, "Hi, what ales are you serving?");
		playMessages(p, n, false, "Well you can either have a nice Asagarnian Ale, or a Wizards Mind Bomb", "Or a Dwarven Stout");

		String[] options = new String[] { "One Asgarnian Ale please", "I'll try the mind bomb", "Can I have a Dwarven Stout?", "I don't feel like any of those" };

		p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);

					switch(option) {
						case 0:
							playMessages(owner, n, false, "That'll be two gold");

							if(owner.getInventory().remove(10, 2) > -1) {
								owner.getActionSender().sendMessage("You buy a pint of Asgarnian Ale");
								owner.getInventory().add(new InvItem(267, 1));
								owner.getActionSender().sendInventory();
							} else {
								playMessages(owner, n, true, notEnoughMoney);
							}

							n.unblock();
							break;
						case 1:
							playMessages(owner, n, false, "That'll be two gold");

							if(owner.getInventory().remove(10, 2) > -1) {
								owner.getActionSender().sendMessage("You buy a pint of Wizard's Mind Bomb");
								owner.getInventory().add(new InvItem(268, 1));
								owner.getActionSender().sendInventory();
							} else {
								playMessages(owner, n, true, notEnoughMoney);
							}

							n.unblock();
							break;
						case 2:
							playMessages(owner, n, false, "That'll be three gold");

							if(owner.getInventory().remove(10, 3) > -1) {
								owner.getActionSender().sendMessage("You buy a pint of Dwarven Stout");
								owner.getInventory().add(new InvItem(269, 1));
								owner.getActionSender().sendInventory();
							} else {
								playMessages(owner, n, true, notEnoughMoney);
							}

							n.unblock();
							break;
						case 3:
							n.unblock();
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
		return n.getID() == 142;
	}

}
