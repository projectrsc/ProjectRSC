package org.darkquest.gs.plugins.npcs;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class Baraek extends Scriptable implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == 26) {
			n.blockedBy(p);
			
			String[] options;
			if (p.getInventory().hasItemId(146)) {
				options = new String[] { "Can you sell me some furs?", "Hello I am in search of a quest", "Would you like to buy my fur?" };
			} else {
				options = new String[] { "Can you sell me some furs?", "Hello I am in search of a quest" };
			}
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					
					switch (option) {
					case 0:
						playMessages(owner, n, false, "Yeah sure they're 20 gold coins a piece");
						
						String[] options = new String[] { "Yeah ok here you go", "20 gold coins that's an outrage" };
						owner.setMenuHandler(new MenuHandler(options) {
							@Override
							public void handleReply(int option, String reply) {
								owner.setBusy(true);
								playMessages(owner, n, true, reply);
								
								switch (option) {
								case 0:
									if (owner.getInventory().remove(10, 20) > -1) {
										owner.getActionSender().sendMessage("You buy a fur from Baraek");
										owner.getInventory().add(new InvItem(146));
										owner.getActionSender().sendInventory();
									} else {
										playMessages(owner, n, true, "oh dear, i don't have enough coins");
									}
									n.unblock();
									break;
								case 1:
									playMessages(owner, n, false, "Well, okay I'll go down to 18");
									
									String[] options = new String[] { "Ok here you go", "No thanks, I'll leave it" };
									owner.setMenuHandler(new MenuHandler(options) {
										@Override
										public void handleReply(int option, String reply) {
											owner.setBusy(true);
											playMessages(owner, n, true, reply);
											
											switch (option) {
											case 0:
												if (owner.getInventory().remove(10, 18) > -1) {
													owner.getActionSender().sendMessage("You buy a fur from Baraek");
													owner.getInventory().add(new InvItem(146));
													owner.getActionSender().sendInventory();
												} else {
													playMessages(owner, n, true, "oh dear, i don't have enough coins");
												}
												break;
											case 1:
												playMessages(owner, n, false, "it's your loss mate");
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
						owner.getActionSender().sendMenu(options);
						break;
					case 1:
						playMessages(owner, n, false, "sorry kiddo, i'm a fur trader not a damsel in distress");
						n.unblock();
						break;
					case 2:
						if (!owner.getInventory().hasItemId(146)) {
							owner.setSuspicious(true);
						} else {
							playMessages(owner, n, false, "Lets have a look at it");
							owner.getActionSender().sendMessage("Baraek examines a fur");
							sleep(2000);
							playMessages(owner, n, false, "it's not in the best of condition", "i guess i could give 12 coins to take it off your hands");

							options = new String[] { "Yeah that'll do", "I think I'll keep hold of it actually" };
							owner.setMenuHandler(new MenuHandler(options) {
								@Override
								public void handleReply(int option, String reply) {
									owner.setBusy(true);
									playMessages(owner, n, true, reply);
									
									switch (option) {
									case 0:
										owner.getActionSender().sendMessage("You give Baraek a fur");
										owner.getInventory().remove(146, 1);
										owner.getActionSender().sendInventory();
										sleep(1500);
										owner.getActionSender().sendMessage("And he gives you twelve coins");
										owner.getInventory().add(new InvItem(10, 12));
										owner.getActionSender().sendInventory();
										break;
									case 1:
										playMessages(owner, n, false, "oh ok", "didn't want it anyway");
										break;
									}
									owner.setBusy(false);
									n.unblock();
								}
							});
							owner.getActionSender().sendMenu(options);
						}
						break;
					}
					owner.setBusy(false);
				}
			});
			p.getActionSender().sendMenu(options);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 26;
	}

}
