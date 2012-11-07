package org.darkquest.gs.plugins.quests;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;

public final class CooksAssistant extends Quest implements TalkToNpcExecutiveListener, InvUseOnObjectExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.COOKS_ASSISTANT;
	}

	@Override
	public String getQuestName() {
		return "Cook's assistant";
	}
	
	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getActionSender().sendMessage("You give some milk, an egg, and some flour to the cook");
		sleep(500);
		player.getInventory().remove(22, 1);
		player.getInventory().remove(19, 1);
		player.getInventory().remove(136, 1);
		player.getActionSender().sendInventory();
		
		player.getActionSender().sendMessage("Well done. You have completed the cook's assistant quest");
		
		player.getActionSender().sendMessage("@gre@You just advanced 1 quest point!");
		player.incQuestPoints(1);
		
		player.incExp(7, 270, false, false, false);
		player.getActionSender().sendStat(7);
	}

	@Override
	public boolean blockTalkToNpc(Player p, final Npc n) {
		if(n.getID() == 7) {
			p.setBusy(true);
			n.blockedBy(p);
			
			switch (p.getQuestStage(this)) {
			case 0:
				playMessages(p, n, false, "What am i to do?");
				
				String[] options = new String[] { "What's wrong?", "Well you could give me all your money", "You don't look very happy", "Nice hat" };
				p.setMenuHandler(new MenuHandler(options) {
					@Override
					public void handleReply(int option, String reply) {
						owner.setBusy(true);
						playMessages(owner, n, true, reply);
						
						switch (option) {
						case 0:
							playCooksRequestToHelp(owner, n);
							break;
						case 1:
							playMessages(owner, n, false, "Haha very funny");
							n.unblock();
							break;
						case 2:
							playMessages(owner, n, false, "No i'm not");
							playCooksRequestToHelp(owner, n);
							break;
						case 3:
							playMessages(owner, n, false, "Err thank you -it's a pretty ordinary cooks hat really");
							n.unblock();
							break;
						}
						
						owner.setBusy(false);
					}
				});
				p.getActionSender().sendMenu(options);
				break;
			case 1:
				playMessages(p, n, false, "how are you getting on with finding those ingredients?");
				
				boolean milk = p.getInventory().hasItemId(22);
				boolean eggs = p.getInventory().hasItemId(19);
				boolean flour = p.getInventory().hasItemId(136);
				if (!milk && !flour && !eggs) {
					playMessages(p, n, true, "i'm afraid i don't have any yet!");
					playMessages(p, n, false, "oh dear oh dear!", "i need flour, eggs, and milk", "without them i am doomed!");
				} else if (milk && flour && eggs) {
					playMessages(p, n, true, "i now have everything you need for your cake", "milk, flour, and an egg!");
					playMessages(p, n, false, "i am saved thankyou!");
					p.sendQuestComplete(getQuestId());
					p.setQuestStage(getQuestId(), -1);
				} else {
					playMessages(p, n, true, "i have found some of the things you asked for:");
					
					if (milk) {
						playMessages(p, n, true, "i have some milk");
					}
					if (flour) {
						playMessages(p, n, true, "i have some flour");
					}
					if (eggs) {
						playMessages(p, n, true, "i have an egg");
					}
					
					playMessages(p, n, false, "great, but can you get the other ingredients as well?", "you still need to find");
					
					if (!milk) {
						playMessages(p, n, false, "some milk");
					}
					if (!flour) {
						playMessages(p, n, false, "some flour");
					}
					if (!eggs) {
						playMessages(p, n, false, "an egg");
					}
					
					playMessages(p, n, true, "ok i'll try to find that for you");
				}
				n.unblock();
				break;
			}
			
			p.setBusy(false);
			return true;
		}
		return false;
	}
	
	private void playCooksRequestToHelp(Player p, final Npc n) {
		playMessages(p, n, false, "Ooh dear i'm in a terrible mess", "it's the duke's bithday today", "i'm meant to be making him a big cake for this evening", "unfortunately, i've forgotten to buy some of the ingredients", "i'll never get them in time now", "i don't suppose you could help me?");
		
		String[] options = new String[] { "Yes, I'll help you", "No, i don't feel like it. Maybe later" };
		p.setMenuHandler(new MenuHandler(options) {
			@Override
			public void handleReply(int option, String reply) {
				owner.setBusy(true);
				playMessages(owner, n, true, reply);
				
				switch (option) {
				case 0:
					playMessages(owner, n, false, "oh thank you, thank you", "i need milk, eggs, and flour", "i'd be very grateful if you could get them to me");
					owner.setQuestStage(getQuestId(), 1);
					break;
				case 1:
					playMessages(owner, n, false, "ok, suit yourself");
					break;
				}
				owner.setBusy(false);
				n.unblock();
			}
		});
		p.getActionSender().sendMenu(options);
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, InvItem item, Player player) {
		return false;
	}
	
}
