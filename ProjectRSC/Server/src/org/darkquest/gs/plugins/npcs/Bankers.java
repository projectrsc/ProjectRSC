package org.darkquest.gs.plugins.npcs;

import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Bankers extends Scriptable implements TalkToNpcExecutiveListener, TalkToNpcListener {

	private final String[] firstOptions = new String[] { "I'd like to access my bank account please", "What is this place?" };

	private final String[] secondOptions = new String[] { "And what do you do?", "Didn't you used to be called the bank of Varrock?" };

	@Override
	public boolean blockTalkToNpc(final Player player, final Npc npc) {
		switch (npc.getID()) {
		case 95:
		case 224:
		case 268: 
		case 485:
		case 540:
		case 617:
		case 792:
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		/*switch (npc.getID()) {
		case 95:
		case 224:
		case 268:
		case 485:
		case 540:
		case 617:
		case 792:
			break;
		default:
			return; 
		} */
		player.setBusy(true);
		npc.blockedBy(player);

		playMessages(player, npc, false, "Good day, how may I help you?");
		
		player.setMenuHandler(new MenuHandler(firstOptions) {
			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, npc, true, reply);
				
				switch (option) {
				case 0:
					playMessages(owner, npc, false, "Certainly " + (owner.isMale() ? "Sir" : "Miss"));

					owner.setAccessingBank(true);
					owner.getActionSender().showBank();
					npc.unblock();
					break;
				case 1:
					playMessages(owner, npc, false, "This is a branch of the bank of Runescape", "We have branches in many towns");
					
					owner.setMenuHandler(new MenuHandler(secondOptions) {
						@Override
						public void handleReply(int option, String reply) {
							owner.setBusy(true);
							playMessages(owner, npc, true, reply);
							
							switch (option) {
							case 0:
								playMessages(owner, npc, false, "We will look after your items and money for you", "So leave your valuables with us if you want to keep them safe");
								break;
							case 1:
								playMessages(owner, npc, false, "Yes we did, but people kept on coming into our branches outside of varrock", "And telling us our signs were wrong", "As if we didn't know what town we were in or something!");
								break;
							}
							owner.setBusy(false);
							npc.unblock();
						}
					});
					owner.getActionSender().sendMenu(secondOptions);
					break;
				}
				
				owner.setBusy(false);
			}
		});
		player.getActionSender().sendMenu(firstOptions);
		player.setBusy(false);
	}


}
