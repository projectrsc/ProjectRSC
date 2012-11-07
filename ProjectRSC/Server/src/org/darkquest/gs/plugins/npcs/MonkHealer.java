package org.darkquest.gs.plugins.npcs;

import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.world.World;

public class MonkHealer implements TalkToNpcListener, TalkToNpcExecutiveListener {
	/**
	 * World instance
	 */
	public World world = World.getWorld(); 

	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		if(npc.getID() != 93)
			return;
		player.informOfNpcMessage(new ChatMessage(npc, "Greetings traveller", player));
		player.setBusy(true);
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] { "Can you heal me? I'm injured" };
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy()) {
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true);
						world.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								if (option == 0) {
									owner.informOfNpcMessage(new ChatMessage(npc, "Ok", owner));
									owner.getActionSender().sendMessage("The monk places his hands on your head");
									world.getDelayedEventHandler().add(new ShortEvent(owner) {
										public void action() {
											owner.setBusy(false);
											owner.getActionSender().sendMessage("You feel a little better");
											int newHp = owner.getCurStat(3) + 10;
											if (newHp > owner.getMaxStat(3)) {
												newHp = owner.getMaxStat(3);
											}
											owner.setCurStat(3, newHp);
											owner.getActionSender().sendStat(3);
											npc.unblock();
										}
									});
								} else {
									owner.setBusy(false);
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
		return n.getID() == 93;
	}
}
