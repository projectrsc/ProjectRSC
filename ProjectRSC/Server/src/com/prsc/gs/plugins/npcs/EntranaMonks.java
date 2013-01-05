package com.prsc.gs.plugins.npcs;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.World;

import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.listeners.action.TalkToNpcListener;
import com.prsc.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.prsc.gs.tools.DataConversions;

public class EntranaMonks implements TalkToNpcListener, TalkToNpcExecutiveListener {

	public World world = World.getWorld();
	
	int[] monks = new int[]{ 212 };
	
	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		//if(npc.getID() != 212) {
		//	return;
		//}
		player.sendMemberErrorMessage();
		/*
		final boolean toEntrana = !player.getLocation().inBounds(390,530, 440, 580); 
		player.informOfNpcMessage(new ChatMessage(npc, toEntrana ? "Are you looking to take passage to our holy island?" : "Are you ready to go back to the mainland?", player));
		player.setBusy(true);
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() { 
				owner.setBusy(false); 
				String[] options = {"Yes okay I'm ready to go", "No thanks"};
				owner.setMenuHandler(new MenuHandler(options) { 
					public void handleReply(final int option, final String reply) {
						if(owner.isBusy()) { 
							npc.unblock(); 
							return; 
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true); 
						world.getDelayedEventHandler().add(new ShortEvent(owner) { 
							public void action() { 
								if(option == 0) {
									owner.getActionSender().sendMessage("You board the ship");
									world.getDelayedEventHandler().add(new ShortEvent(owner) {
										public void action() { 
											if(toEntrana) { 
												owner.teleport(418,570, false); 
											} 
											else { 
												owner.teleport(263, 659, false); 
											}
											owner.getActionSender().sendMessage("The ship arrives at " + (toEntrana ? "Entrana" : "Port Sarim")); 
											owner.setBusy(false);
											npc.unblock(); 
										}
									}); 
								}
								else { 
									owner.setBusy(false);
									npc.unblock(); 
									} 
								}
							}); 
						}
					});
			    owner.getActionSender().sendMenu(options); } 
			});
		npc.blockedBy(player);
		return;
		
		*/
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(monks, n.getID()); 
	}
}
