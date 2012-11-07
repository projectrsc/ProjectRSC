package org.darkquest.gs.plugins.minigames;

import java.util.List;

import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.model.Bubble;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.plugins.Scriptable;
import org.darkquest.gs.plugins.listeners.action.InvUseOnObjectListener;
import org.darkquest.gs.plugins.listeners.action.PlayerKilledNpcListener;
import org.darkquest.gs.plugins.listeners.action.TalkToNpcListener;
import org.darkquest.gs.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import org.darkquest.gs.plugins.listeners.executive.TalkToNpcExecutiveListener;
import org.darkquest.gs.world.World;

public final class TutorialIsland extends Scriptable implements TalkToNpcExecutiveListener, TalkToNpcListener, PlayerKilledNpcExecutiveListener, PlayerKilledNpcListener, InvUseOnObjectExecutiveListener, InvUseOnObjectListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() != 476 || n.getID() != 474 || n.getID() != 478 || n.getID() != 479 || n.getID() != 480 || n.getID() != 499) {
			return;
		}
		p.setBusy(true);
		n.blockedBy(p);

		switch (n.getID()) {
		case 476:
			playMessages(p, n, false, "welcome to the world of runescape", "my job is to help newcomers find their feet here");
			playMessages(p, n, true, "ah good, let's get started");
			playMessages(p, n, false, "when speaking to characters such as myself", "sometimes options will appear in the top left corner of the screen", "left click on one of them to continue the conversation");

			String[] options = { "So what else can you tell me?", "What other controls do I have?" };
			p.setMenuHandler(new MenuHandler(options) {
				@Override
				public void handleReply(int option, String reply) {
					owner.setBusy(true);
					playMessages(owner, n, true, reply);
					playMessages(owner, n, false, "i suggest you go through the door now", "there are several guides and advisors on the island", "speak to them", "they will teach you about various aspects of the game");
					owner.getActionSender().sendAlert("Use the quest history tab at the bottom of the screen to reread things said to you by ingame characters", false);
					if (!owner.getCache().hasKey("tutorial")) {
						owner.getCache().store("tutorial", 0);
					}
					owner.setBusy(false);
					n.unblock();
				}
			});
			p.getActionSender().sendMenu(options);
			break;
		case 499:
			playMessages(p, n, false, "hello i'm here to tell you more about the game's controls", "most of your options and character information", "can be accesed by the menus in the top right corner of your screen", "moving your mouse over the map icon", "which is the second icon from the right", "gives you a view of the area you are in", "clicking on this map is an effective way of walking around", "though if the route is blocked, for example by a closed door", "then your character wont move", "also notice the compass on the map which may be of help to you");
			playMessages(p, n, true, "thankyou for your help");
			playMessages(p, n, false, "now carry on to speak to the combat instructor");
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 0) {
				p.getCache().update("tutorial", 1);
			}
			n.unblock();
			break;
		case 474:
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 1) {
				boolean wearingSword = false, wearingShield = false;
				List<InvItem> items = p.getInventory().getItems();

				synchronized (items) {
					for (InvItem item : items) {
						if (item.isWielded()) {
							if (item.getID() == 70) {
								wearingSword = true;
							} else if (item.getID() == 4) {
								wearingShield = true;
							}
						}
					}
				}

				if (!wearingSword || !wearingShield) {
					if (p.getInventory().countId(70) == 0 || p.getInventory().countId(4) == 0) {
						playMessages(p, n, false, "aha a new recruit", "i'm here to teach you the basics of fighting", "first of all you need weapons");
						p.getActionSender().sendMessage("The instructor gives you a sword and shield");
						if (p.getInventory().countId(4) == 0) {
							p.getInventory().add(new InvItem(4));
						}
						if (p.getInventory().countId(70) == 0) {
							p.getInventory().add(new InvItem(70));
						}
						p.getActionSender().sendInventory();
						sleep(2000);
						playMessages(p, n, false, "look after these well", "these items will now have appeared in your inventory", "you can access them by selecting the bag icon in the menu bar", "which can be found in the top right hand cordner of the screen");
					}
					playMessages(p, n, false, "to wield your weapon and shield left click on them within your inventory", "thier box will go red to show you are wearing them");
					p.getActionSender().sendMessage("When you have done this speak to the combat instructor again");
				} else {
					p.informGroupOfNpcMessage(new ChatMessage(n, "today we're going to be killing giant rats", p));
					sleep(500);

					for (Npc rat : p.getViewArea().getNpcsInView()) {
						if (rat.getID() == 473) {
							p.informGroupOfNpcMessage(new ChatMessage(rat, "squeek", p));
							break;
						}
					}
					sleep(1500);
					playMessages(p, n, false, "move your mouse over a rat you will see it is level 7", "you will see it's level is written in green", "if it is green this means you have a strong chance of killing it", "creatures with their name in red should probably be avoided", "as this indicates they are tougher than you", "left click on the rat to attack it");
				}
			} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") >= 2) {
				playMessages(p, n, false, "well done you're a born fighter", "as you kill things", "your combat experience will go up", "this experience will slowly cause you to get tougher", "eventually you will be able to take on stronger enemies", "such as those found in dungeons", "now continue to the building to the northeast");
				if (p.getCache().getInt("tutorial") == 2) {
					p.getCache().update("tutorial", 3);
				}
			}
			n.unblock();
			break;
		case 478:
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 3) {
				if (p.getInventory().hasItemId(134)) {

				} else {
					playMessages(p, n, false, "looks like you've been fighting", "if you get hurt in a fight", "you will slowly heal", "eating food will heal you more quickly", "i'm here to show you some simple cooking", "first you need something to cook");
					if (!p.getInventory().hasItemId(503)) {
						p.getActionSender().sendMessage("the instructor gives you a piece of meat");
						p.getInventory().add(new InvItem(503));
						p.getActionSender().sendInventory();
					}
					playMessages(p, n, false, "ok cook it on the range", "to use an item you are holding", "open your inventory and click on the item you wish to use", "then click on whatever you wish to use it on", "in this case use it on the range");
				}
			} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 4) {
				playMessages(p, n, true, "i burnt the meat");
				playMessages(p, n, false, "well i'm sure you'll get the hang of it soon", "let's try again", "here's another piece of meat to cook");
				if (!p.getInventory().hasItemId(503)) {
					p.getInventory().add(new InvItem(503));
					p.getActionSender().sendInventory();
				}
			} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 5) {
				playMessages(p, n, true, "i've cooked the meat correctly this time");
				playMessages(p, n, false, "very well done", "now you can tell whether you need to eat it or not", "look in your stats menu", "click on the bar graph icon in the menu bar", "your stats are low right now", "as you use the various skills, these stats will increase", "if you look at your hits you will see 2 numbers", "the number on the right is your hits when you are at full health", "the number on the left is your current hits", "if the number on the left is lower eat some food to be healed");
				p.getCache().update("tutorial", 6);
			} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") >= 6) {
				playMessages(p, n, false, "there are many other sorts of food you can cook", "as your cooking level increases you will be able to cook even more", "some of these dishes are more complicated to prepare", "if you want to know about cookery", "you could consult the online manual", "now proceed through the next door");
				p.getCache().update("tutorial", 7);
			}
			n.unblock();
			break;
		case 480:
			playMessages(p, n, false, "hello there", "i'm your designated financial advisor");
			playMessages(p, n, true, "that's good because i don't have any money at the moment", "how do i get rich?");
			playMessages(p, n, false, "there are many different ways to make money in runescape", "for example certain monsters will drop a bit of loot", "to start with killing men and goblins might be a good idea", "some higher level monsters will drop quite a lot of treasure", "several of runescapes skills are good money making skills", "two of these skills are mining and fishing", "there are two instructors on the island who will help you with this", "using skills and combat to make money is a good plan", "because using a skill also slowly increases your level in that skill", "a high level in a skill opens up many more oppurtunities", "some other ways of making money include taking quests and tasks", "you can find these by talking to certain game controlled characters", "our quest advisors will tell you about this", "sometimes you will find items lying around", "selling these to the shops makes money too", "now continue through the next door");
			p.getCache().update("tutorial", 8);
			n.unblock();
			break;
		case 479:
			playMessages(p, n, true, "hi are you here to tell me how to catch fish?");
			playMessages(p, n, false, "yes that's right, you're a smart one", "fishing is a useful skill", "you can sell high level fish for lots of money", "or of coarse you can cook it and eat it to heal yourself", "unfortunatly you'll have to start off catching shrimps", "till your fishing level gets higher", "you'll need this");
			n.unblock();
			break;
		}

		p.setBusy(false);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 476 || n.getID() == 474 || n.getID() == 478 || n.getID() == 479 || n.getID() == 480 || n.getID() == 499) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 1) {
			p.setBusy(true);
			p.getActionSender().sendMessage("Well done you've killed the rat");
			World.getWorld().getDelayedEventHandler().add(new ShortEvent(p) {
				@Override
				public void action() {
					owner.getActionSender().sendMessage("Now speak to the combat instructor again");
					owner.getCache().update("tutorial", 2);
					owner.setBusy(false);
				}
			});
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == 473;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, InvItem item, Player p) {
		/*
		p.setBusy(true);
		p.informGroupOfBubble(new Bubble(p, item.getID()));
		p.getInventory().remove(item);
		p.getActionSender().sendInventory();

		World.getWorld().getDelayedEventHandler().add(new ShortEvent(p) {
			@Override
			public void action() {
				if (owner.getCache().hasKey("tutorial") && owner.getCache().getInt("tutorial") == 3) {
					owner.getActionSender().sendMessage("You accidentally burn the meat");
					owner.getInventory().add(new InvItem(134));
					owner.getActionSender().sendInventory();
					sleep(1500);
					owner.getActionSender().sendMessage("sometimes you will burn food");
					sleep(1500);
					owner.getActionSender().sendMessage("As your cooking level increases this will happen less");
					sleep(1500);
					owner.getActionSender().sendMessage("Now speak to the cooking instructor again");
					owner.getCache().update("tutorial", 4);
				} else {
					owner.getActionSender().sendMessage("The meat is now nicely cooked");
					owner.getInventory().add(new InvItem(132));
					owner.getActionSender().sendInventory();

					if (owner.getCache().hasKey("tutorial") && owner.getCache().getInt("tutorial") == 4) {
						sleep(1500);
						owner.getActionSender().sendMessage("Now speak to the cooking instructor again");
						owner.getCache().update("tutorial", 5);
					}
				}
				owner.setBusy(false);
			}
		});
		*/
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, InvItem item, Player player) {
		return false;//player.getLocation().onTutorialIsland() && item.getID() == 503 && obj.getID() == 11;
	}

}
