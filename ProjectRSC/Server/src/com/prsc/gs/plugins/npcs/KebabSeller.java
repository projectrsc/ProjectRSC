package com.prsc.gs.plugins.npcs;

import com.prsc.gs.event.ShortEvent;

import com.prsc.gs.model.ChatMessage;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class KebabSeller implements TalkToNpcListener, TalkToNpcExecutiveListener {
	/**
	 * World instance
	 */
	public World world = World.getWorld();

	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		//if(npc.getID() != 90)
		//	return;
		player.informOfNpcMessage(new ChatMessage(npc, "Would you like to buy a nice kebab? Only 1 gold", player));
		player.setBusy(true);
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] { "I think I'll give it a miss", "Yes please" };
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy()) {
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true);
						world.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.setBusy(false);
								if (option == 1) {
									if (owner.getInventory().remove(10, 1) > -1) {
										owner.getActionSender().sendMessage("You buy a kebab");
										owner.getInventory().add(new InvItem(210, 1));
										owner.getActionSender().sendInventory();
										npc.unblock();
									} else {
										owner.informOfChatMessage(new ChatMessage(owner, "Oops I forgot to bring any money with me", npc));
										owner.setBusy(true);
										world.getDelayedEventHandler().add(new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												owner.informOfNpcMessage(new ChatMessage(npc, "Come back when you have some", owner));
												npc.unblock();
											}
										});
									}
								} else {
									npc.unblock();
								}
							}
						});
					}
				});
				owner.getActionSender().sendMenu(options);
			}
		});
		npc.blockedBy(player);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 90; 
	}

}
