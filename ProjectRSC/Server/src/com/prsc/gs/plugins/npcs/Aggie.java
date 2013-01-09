package com.prsc.gs.plugins.npcs;


import com.prsc.gs.event.MiniEvent;
import com.prsc.gs.event.ShortEvent;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.model.ChatMessage;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.prsc.gs.world.World;

public final class Aggie implements TalkToNpcListener, TalkToNpcExecutiveListener {

	public World world = World.getWorld();

	int[] dyes = { 238, 239, 272 }; 

	int[] itemReq = { 236, 241, 281 };

	String[] names = { "Red dye please", "Yellow dye please", "Blue dye please", "No thanks" };

	int ourOption = -1;
	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		try {
			//if(npc.getID() != 125)
			//	return;
			player.setBusy(false);
			player.informOfNpcMessage(new ChatMessage(npc, "Hi traveller, i specialize in creating different colored dyes", player));
			Thread.sleep(1500);
			player.informOfNpcMessage(new ChatMessage(npc, "Would you like me to create you any dyes?", player));
			player.setMenuHandler(new MenuHandler(names) {
				public void handleReply(final int option, final String reply) {
					if (option < 0 && option > names.length || option == 3)
						return;
					if(reply.equalsIgnoreCase("null")) { // temp fix until switch over
						return;
					}
					ourOption = option;
					owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
					world.getDelayedEventHandler().add(new ShortEvent(owner) {
						public void action() {
							owner.informOfNpcMessage(new ChatMessage(npc, "You will need 1 " + EntityHandler.getItemDef(itemReq[ourOption]).getName() + " and 5gp for me to create this dye", owner));
							world.getDelayedEventHandler().add(new ShortEvent(owner) {
								public void action() {
									String[] diag = { "Yes i have them", "Ill come back when i have the ingrediants" };
									owner.setMenuHandler(new MenuHandler(diag) {
										public void handleReply(final int option, final String reply) {
											if (option == 0) {
												owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
												world.getDelayedEventHandler().add(new ShortEvent(owner) {
													public void action() {
														if (owner.getInventory().countId(itemReq[ourOption]) < 1 || owner.getInventory().countId(10) < 5) {
															owner.informOfNpcMessage(new ChatMessage(npc, "It seems like you don't have all what's Required, come back later.", owner));
															owner.setBusy(false);
															npc.setBusy(false);
															npc.unblock();
															return;
														}
														owner.informOfNpcMessage(new ChatMessage(npc, "Here is your new Dye, enjoy.", owner));
														world.getDelayedEventHandler().add(new MiniEvent(owner, 1000) {
															public void action() {
																if (owner.getInventory().remove(itemReq[ourOption], 1) > -1 && owner.getInventory().remove(10, 5) > -1) {
																	owner.getInventory().add(new InvItem(dyes[ourOption]));
																	owner.getActionSender().sendInventory();
																}
															}
														});
														owner.setBusy(false);
														npc.setBusy(false);
														npc.unblock();
														return;
													}
												});
											} 
											else {
												owner.setBusy(false);
												npc.setBusy(false);
												npc.unblock();
												return;
											}
										}
									});
									owner.getActionSender().sendMenu(diag);
								}
							});
						}
					});
				}
			});
			player.getActionSender().sendMenu(names);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 125;
	}
}
