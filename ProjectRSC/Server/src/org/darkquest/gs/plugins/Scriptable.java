package org.darkquest.gs.plugins;

import org.darkquest.config.Formulae;
import org.darkquest.gs.event.DelayedEvent;


import org.darkquest.gs.event.SingleEvent;
import org.darkquest.gs.external.EntityHandler;
import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.MenuHandler;
import org.darkquest.gs.model.Mob;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.states.CombatState;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.World;
import org.python.core.PyInteger;
import org.python.core.PyObject;

public abstract class Scriptable { 

	protected Player participant = null;
	
	protected Npc npc = null;

	public enum SkillType { // Should be moved to Player
		ATTACK(0), DEFENSE(1), STRENGTH(2), HITS(3), RANGED(4),
		PRAYER(5), MAGIC(6), COOKING(7), WOODCUT(8), FLETCHING(9),
		FISHING(10), FIREMAKING(11), CRAFTING(12), SMITHING(13), MINING(14), HERBLAW(15),
		AGILITY(16), THIEVING(17);

		protected int stat = 0;

		SkillType(int stat) {
			this.stat = stat;
		}

		public int getSkill() { 
			return stat; 
		}
	}

	/**
	 * Helper methods 
	 * @param delay
	 */

	public void setParticipant(Player participant) {
		this.participant = participant;
	}

	public void setParticipants(Player participant, Npc npc) {
		this.participant = participant;
		this.npc = npc;
	}

	public Player getParticipant() {
		return participant;
	}

	public void setActiveNpc(Npc npc) {
		this.npc = npc;
	}

	public Npc getActiveNpc() {
		return npc;
	}

	public void displayMessage(String... messages) {
		displayMessage(messages, 500);
	}
	
	public void displayMessage(String[] messages, int delay) {
		for(String message : messages) {
			participant.getActionSender().sendMessage(message);
			if(delay > 0)
				sleep(delay);
		}
	}

	/*
	public void displayMessage(String message) {
		participant.getActionSender().sendMessage(message);
	}
	*/

	public void displayAlert(String message) {
		displayAlert(message, false);
	}

	public void displayAlert(String message, boolean big) {
		participant.getActionSender().sendAlert(message, big);
	}

	public void sendChat(Mob speaker, Mob receiver, String... messages) {
		for(String message : messages) {
			if(speaker instanceof Player) {
				participant.informGroupOfChatMessage(new ChatMessage(speaker, message, receiver));
			} else {
				participant.informGroupOfNpcMessage(new ChatMessage(speaker, message, receiver));
			}
			sleep(2200);
		}
	}

	public void sendChat(Mob speaker, String... messages) {
		for(String message : messages) {
			if(speaker instanceof Player) {
				participant.informGroupOfChatMessage(new ChatMessage(speaker, message, npc));
			} else {
				participant.informGroupOfNpcMessage(new ChatMessage(speaker, message, participant));
			}
			sleep(2200);
		}
	}

	public void sendNpcChat(String... messages) {
		for(String message : messages) {
			participant.informGroupOfNpcMessage(new ChatMessage(npc, message, participant));
			sleep(2200);
		}
	}

	public void sendPlayerChat(String... messages) {
		for(String message : messages) {
			participant.informGroupOfChatMessage(new ChatMessage(participant, message, npc));
			sleep(2200);
		}
	}
	
	@Deprecated // wait til hikilaka and I update ours
	public void pickOption(final String[] messages, final PyObject handler) {	
		participant.setMenuHandler(new MenuHandler(messages) {
			@Override
			public void handleReply(int option, String reply) {
				participant.setBusy(true);
				sendChat(participant, npc, reply);
				handler.__call__(new PyInteger(option));
			}
		});
		participant.setBusy(false);
		participant.getActionSender().sendMenu(messages);
	}
	
	public int pickOption(String[] strs, boolean repeat) {
		try {
			long time = System.currentTimeMillis();
			participant.setBusy(false);
			participant.lastOption = -2;
			participant.setMenuHandler(new MenuHandler(strs) {
				public void handleReply(int option, String reply) {
					if (option < 0 || option >= getOptions().length || option == 30) {
						npc.unblock();
						participant.setBusy(false);
						owner.lastOption = -1;
						return;
					} else {
						owner.lastOption = option;
					}
				}
			});
			participant.getActionSender().sendMenu(strs);
			while (participant.lastOption == -2 && System.currentTimeMillis() - time < 20000) { // timeout
				sleep(12);
			}
			if (participant.lastOption == -1 || participant.lastOption == -2) {
				participant.setBusy(false);
				npc.unblock();
				return -1;
			} 
			participant.setBusy(true);
			int newOpt = participant.lastOption;
			participant.lastOption = -2;
			if(repeat)
				sendChat(participant, strs[newOpt]);
			return newOpt;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int pickOption(String[] strs) {
		return pickOption(strs, true);
	}

	public int getRemainingSpace() {
		return 30 - participant.getInventory().size();
	}

	public void removeItem(int id, int amount) {
		if (EntityHandler.getItemDef(id).isStackable()) {
			participant.getInventory().remove(id, amount);
		} else {
			for (int i = 0; i < amount; i++) {
				participant.getInventory().remove(id, 1);
			}
		}
		participant.getActionSender().sendInventory();
	}

	public void removeAllItem(int id) {
		removeItem(id, countItem(id));
	}

	public void spawnItem(int x, int y, int id, int amount) {
		spawnItem(x, y, id, amount, 500);
	}

	public void spawnItem(final int x, final int y, final int id, final int amount, final int spawnFor) {
		final InvItem item = new InvItem(id, amount);

		World.getWorld().getDelayedEventHandler().add(new DelayedEvent(participant, spawnFor) {
			public void run() {
				world.registerItem(new Item(item.getID(), x, y, item.getAmount(), participant));
				matchRunning = false;
			}
		}); 
	}

	public void addItem(int id, int amount) {
		participant.getInventory().add(new InvItem(id, amount));
		participant.getActionSender().sendInventory();
	}
	
	public void addItem(int id) {
		addItem(id, 1);
	}

	public boolean hasItem(int id) {
		return hasItem(id, 1);
	}
	
	public int rand(int low, int high) {
		return Formulae.Rand(low, high);
	}

	public boolean hasItem(int id, int amount) {
		if (EntityHandler.getItemDef(id).isStackable()) {
			for (InvItem i : participant.getInventory().getItems()) {
				if (i.getID() == id && i.getAmount() >= amount)
					return true;
			}
		} else {
			int count = 0;
			for (InvItem i : participant.getInventory().getItems()) {
				if (i.getID() == id)
					count++;
			}
			if (count >= amount)
				return true;
		}
		return false;
	}

	public int countItem(int id) {
		return participant.getInventory().countId(id);
	}

	public int getMaxLevel(SkillType skill) {
		return participant.getMaxStat(skill.getSkill());
	}

	public int getCurrentLevel(SkillType skill) {
		return participant.getCurStat(skill.getSkill());
	}

	public void advanceStat(SkillType skillToAdvance, int experienceAmount) {
		participant.incExp(skillToAdvance.getSkill(), experienceAmount, false, false, false);
		participant.getActionSender().sendStat(skillToAdvance.getSkill());
	}

	public void addQuestPoints(int points) {
		participant.incQuestPoints(points);
		participant.getActionSender().sendQuestPoints();
	}

	public Npc getNpc(int npcId) {
		return World.getWorld().getNpc(npcId);
	}
	
	public void occupy() {
		participant.setBusy(true);
		npc.blockedBy(participant);
	}
	
	public void release() {
		participant.setBusy(false);
		npc.unblock();
	}

	//should we remove these two methods?
	public void blockNpc() {
		if(npc != null && participant != null) {
			System.out.println("Called blockNpc");
			npc.blockedBy(participant);
		}
	}

	public void unblockNpc() {
		if(npc != null && participant != null) {
			System.out.println("Called unblockNpc");
			npc.unblock();
		}
	}

	public Npc spawnNpc(int npcId, int x, int y, boolean respawn) {
		return spawnNpc(npcId, x, y, 0, respawn);
	}

	public Npc spawnNpc(int npcId, int x, int y, int time, boolean respawn) {
		if (EntityHandler.getNpcDef(npcId) != null) {
			final Npc n = new Npc(npcId, x, y, x - 5, x + 5, y - 5, y + 5);
			n.setRespawn(respawn);
			World.getWorld().registerNpc(n);
			if(!respawn) {
				World.getWorld().getDelayedEventHandler().add(new SingleEvent(null, time == 0 ? 300000 : time) {
					public void action() {
						Mob opponent = n.getOpponent();
						if (opponent != null) {
							opponent.resetCombat(CombatState.ERROR);
						}
						n.resetCombat(CombatState.ERROR);
						world.unregisterNpc(n);
						n.remove();
					}
				});
			}
			return n;
		}
		return null;
	}

	public void removeNpc(Npc npc) {
		World.getWorld().unregisterNpc(npc);
		npc.remove();
	}

	public void attackPlayer(final Npc npc) {
		npc.attack(participant);
	}

	public void setBusy(boolean busy) {
		participant.setBusy(busy);
	}

	public void teleport(int x, int y, boolean showBubble) {
		participant.teleport(x, y, showBubble);
	}
	
	public void summoningRitual(int offsetx, int offsety) {
		for(int x = participant.getX(); x < participant.getX() + offsetx; x++) {
			for(int y = participant.getY(); y < participant.getY() + offsety; y++) {
				participant.getActionSender().sendTeleBubble(x, y, false);
			}
		}
	}
	
	public int getRandom(int start, int n) {
		return DataConversions.random(start, n);
	}

	public void sleep(final int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}

	@Deprecated
	protected void playMessages(Player player, Mob mob, boolean playerIsSpeaker, String... messages) {
		for (String message : messages) {
			if (playerIsSpeaker) {
				player.informGroupOfChatMessage(new ChatMessage(player, message, mob));
			} else {
				player.informGroupOfNpcMessage(new ChatMessage(mob, message, player));
			}
			sleep(2200);
		}
	} 
}
