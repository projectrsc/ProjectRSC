package org.darkquest.gs.plugins.npcs;

import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Tramp extends Scriptable implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 28;
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 28) {
			p.setBusy(true);
			n.blockedBy(p);
			
			playMessages(p, n, false, "Spare some change guv?");
			
			String[] options = new String[] { "Sorry I haven't got any", "Go get a job", "Ok here you go", "Is there anything down this alleyway?" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					
					switch (option) {
					case 0:
						playMessages(owner, n, false, "Thanks anyways");
						n.unblock();
						break;
					case 1:
						playMessages(owner, n, false, "You startin?");
						n.unblock();						
						break;
					case 2:
						if (owner.getInventory().remove(10, 1) > -1) {
							owner.getActionSender().sendInventory();
							playMessages(owner, n, false, "Thankyou, that's great");
							
							String[] options = new String[] { "No problem", "So don't I get some sort of quest hint or something now" };
							owner.setMenuHandler(new MenuHandler(options) {
								@Override
								public void handleReply(int option, String reply) {
									owner.setBusy(true);
									playMessages(owner, n, true, reply);
									
									switch (option) {
									case 1:
										playMessages(owner, n, false, "No that's not why I'm asking for money", "I just need to eat");
										break;
									}
									owner.setBusy(false);
									n.unblock();
								}
							});
							owner.getActionSender().sendMenu(options);
						} else {
							n.unblock();
						}
						break;
					case 3:
						playMessages(owner, n, false, "Yes, there is actually", "A notorious gang of thieves and hoodlums", "Called the blackarm gang");
						
						String[] options = new String[] { "Thanks for the warning", "Do you think they would let me join?" };
						owner.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);
								
								switch (option) {
								case 0:
									playMessages(owner, n, false, "Don't worry about it");
									break;
								case 1:
									playMessages(owner, n, false, "You never know", "You'll find a lady down there called Katrine", "Speak to her", "But don't upset her, she's pretty dangerous");
									break;
								}
								owner.setBusy(false);
								n.unblock();
							}
						});
						owner.getActionSender().sendMenu(options);
						break;
					}
					owner.setBusy(false);
				}
			});
			p.getActionSender().sendMenu(options);
			p.setBusy(false);
		}
	}
	
}