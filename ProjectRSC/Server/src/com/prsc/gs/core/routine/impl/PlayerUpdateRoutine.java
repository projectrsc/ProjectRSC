package com.prsc.gs.core.routine.impl;


import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.python.google.common.collect.Lists;

import com.prsc.gs.builders.impl.GameObjectPositionPacketBuilder;
import com.prsc.gs.builders.impl.ItemPositionPacketBuilder;
import com.prsc.gs.builders.impl.NpcPositionPacketBuilder;
import com.prsc.gs.builders.impl.NpcUpdatePacketBuilder;
import com.prsc.gs.builders.impl.PlayerPositionPacketBuilder;
import com.prsc.gs.builders.impl.PlayerUpdatePacketBuilder;
import com.prsc.gs.builders.impl.WallObjectPositionPacketBuilder;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.core.GameEngine;
import com.prsc.gs.core.TaskManager;
import com.prsc.gs.core.routine.Routine;
import com.prsc.gs.model.ChatMessage;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.tools.DataConversions;

public class PlayerUpdateRoutine extends Routine {
	
	private List<Player> players = null;
	private int numWorkers = TaskManager.WORKER_COUNT;
	private Task task = null;
	
	public PlayerUpdateRoutine(List<Player> players, Task task) {
		this.players = players;
		this.task = task;
	}
	
	public enum Task {
		POSITIONS, MESSAGES, OFFERS, VIEWS, COLLECTIONS, APPEARANCES
	}
	
	private void computeDirectly() {
		switch(task) {
		case POSITIONS:
			updatePlayersPositions();
			break;
		case MESSAGES:
			updateMessageQueues();
			break;
		case OFFERS:
			updateOffers();
			break;
		case VIEWS:
			updateViews();
			break;
		case COLLECTIONS:
			updateCollections();
			break;
		case APPEARANCES:
			sendQueuedPackets();
			updateAppearances();
			break;
		}
	}

	@Override
	protected void compute() {
		int maxWorkers = numWorkers * 16; 
		
		if(players.isEmpty()) {
			return;
		}
		
		if(players.size() < maxWorkers) {
			computeDirectly();
			return;
		}
		
		int split = players.size() / numWorkers;
		
		List<PlayerUpdateRoutine> tasks = new ArrayList<PlayerUpdateRoutine>();
		List<List<Player>> o = Lists.partition(players, split);
		
		for(List<Player> sub : o) {
			tasks.add(new PlayerUpdateRoutine(sub, task));
		}
		
		invokeAll(tasks);
	}
	
	private void updatePlayersPositions() {
		for (Player p : players) {
			p.resetMoved();
			p.updatePosition();
			p.updateAppearanceID();
		}
		for (Player p : players) {
			p.revalidateWatchedPlayers();
			p.revalidateWatchedObjects();
			p.revalidateWatchedItems();
			p.revalidateWatchedNpcs();
			p.updateViewedPlayers();
			p.updateViewedObjects();
			p.updateViewedItems();
			p.updateViewedNpcs();
		}
	}
	
	private void updateMessageQueues() {
		for (Player sender : players) {
			ChatMessage message = sender.getNextChatMessage();
			if (message == null || !sender.loggedIn()) {
				continue;
			}
			String s = DataConversions.byteToString(message.getMessage(), 0, message.getMessage().length);
			s = s.toLowerCase();
			String k = s;
			s = s.replace(" ", "");
			s = s.replace(".", "");

			if (sender.isMuted()) {
				return;
			}

			Iterable<Player> recievers = sender.getViewArea().getPlayersInView();
			ArrayList<String> recieverUsernames = new ArrayList<String>();
			for (Player p : recievers)
				recieverUsernames.add(p.getUsername());

			//Services.lookup(DatabaseManager.class).addQuery(new ChatLog(sender.getUsername(), k, recieverUsernames));

			for (Player recipient : recievers) {
				if (sender.getIndex() == recipient.getIndex() || !recipient.loggedIn()) {
					continue;
				}
				if (recipient.getPrivacySetting(0) && !recipient.isFriendsWith(sender.getUsernameHash()) && !sender.isAdmin()) {
					continue;
				}
				if (recipient.isIgnoring(sender.getUsernameHash()) && !sender.isAdmin()) {
					continue;
				}
				recipient.informOfChatMessage(message);
			}
			recievers = null;
		}
	}
	
	private void updateOffers() {
		for (Player player : players) {
			if (!player.requiresOfferUpdate()) {
				continue;
			}
			player.setRequiresOfferUpdate(false);
			
			if (player.isTrading()) {
				Player affectedPlayer = player.getWishToTrade();
				if (affectedPlayer == null) {
					continue;
				}
				affectedPlayer.getActionSender().sendTradeItems();
			} else if(player.isDueling()) {
				Player affectedPlayer = player.getWishToDuel();
				if (affectedPlayer == null) {
					continue;
				}
				player.getActionSender().sendDuelSettingUpdate();
				affectedPlayer.getActionSender().sendDuelSettingUpdate();
				affectedPlayer.getActionSender().sendDuelItems();
			}
		}
	}
	
	private void updateViews() {
		for (Player p : players) { // sending update packets
			updateTimeouts(p);

			updatePlayerPositions(p);
			updateNpcPositions(p);
			updateGameObjects(p);
			updateWallObjects(p);
			updateItems(p);

			p.setFirstMajorUpdateSent(true);
		}
	}
	
	/**
	 * Updates collections, new becomes known, removing is removed etc.
	 */
	private void updateCollections() {
		for (Player p : players) {
			if (p.isRemoved() && p.initialized()) {
				World.getWorld().unregisterPlayer(p);
			}
		}

		for (Player p : players) {
			p.getWatchedPlayers().update();
			p.getWatchedObjects().update();
			p.getWatchedItems().update();
			p.getWatchedNpcs().update();

			p.resetSpriteChanged();
		}
	}
	
	private void updateAppearances() {
		for (Player p : players) {
			p.updateAppearanceID();
		}
		for (Player p : players) {
			if (p.isFirstMajorUpdateSent()) {
				updatePlayerApperances(p);
			}
			updateNpcApperances(p);
		}

		for (Player p : players) {
			if (p.isFirstMajorUpdateSent()) {
				p.setAppearnceChanged(false);
				p.clearProjectilesNeedingDisplayed();
				p.clearPlayersNeedingHitsUpdate();
				p.clearNpcsNeedingHitsUpdate();
				p.clearChatMessagesNeedingDisplayed();
				p.clearNpcMessagesNeedingDisplayed();
				p.clearBubblesNeedingDisplayed();
			}
		}
	}
	
	/**
	 * Update positions of the given player, and any players they should be
	 * aware of
	 */
	private void updatePlayerPositions(Player p) {
		PlayerPositionPacketBuilder playerPositionBuilder = new PlayerPositionPacketBuilder();
		playerPositionBuilder.setPlayer(p);
		RSCPacket temp = playerPositionBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	}
	
	/**
	 * Update appearance of the given player, and any players they should be
	 * aware of
	 */
	
	private void updatePlayerApperances(Player p) {
		try {
			PlayerUpdatePacketBuilder playerApperanceBuilder = new PlayerUpdatePacketBuilder();
			playerApperanceBuilder.setPlayer(p);
			RSCPacket temp = playerApperanceBuilder.getPacket();
			if (temp != null) {
				p.getSession().write(temp);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	} 
	
	/**
	 * Sends updates for npcs to the given player
	 */
	private void updateNpcPositions(Player p) {
		NpcPositionPacketBuilder npcPositionPacketBuilder = new NpcPositionPacketBuilder();
		npcPositionPacketBuilder.setPlayer(p);
		RSCPacket temp = npcPositionPacketBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	}
	
	/**
	 * Update appearance of any npcs the given player should be aware of
	 */
	
	private void updateNpcApperances(Player p) {
		NpcUpdatePacketBuilder npcApperanceBuilder = new NpcUpdatePacketBuilder();
		npcApperanceBuilder.setPlayer(p);
		RSCPacket temp = npcApperanceBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	} 
	
	/**
	 * Sends updates for game objects to the given player
	 */
	private void updateGameObjects(Player p) {
		GameObjectPositionPacketBuilder gameObjectPositionBuilder = new GameObjectPositionPacketBuilder();
		gameObjectPositionBuilder.setPlayer(p);
		RSCPacket temp = gameObjectPositionBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	}

	/**
	 * Sends updates for game items to the given player
	 */
	private void updateItems(Player p) {
		ItemPositionPacketBuilder itemPositionBuilder = new ItemPositionPacketBuilder(p);
		RSCPacket temp = itemPositionBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	}
	
	/**
	 * Sends updates for wall objects to the given player
	 */
	private void updateWallObjects(Player p) {
		WallObjectPositionPacketBuilder wallObjectPositionPacketBuilder = new WallObjectPositionPacketBuilder(p);
		RSCPacket temp = wallObjectPositionPacketBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	}
	
	/**
	 * Checks the player has moved within the last 5mins
	 */
	private void updateTimeouts(Player p) {
		if (p.destroyed()) {
			return;
		}
		
		//long curTime = System.currentMillis();
		long inSeconds = TimeUnit.NANOSECONDS.toSeconds(GameEngine.getAccurateTimestamp());
		
		if (inSeconds - p.getLastPing() >= 30) {
			p.destroy(false); // was false - xent
		} else if (inSeconds - p.getLastPing() >= 60) { // 1 minute force logout
			p.destroy(true);
		} else if(p.warnedToMove()) { // 8 mins
			if (inSeconds - p.getLastMoved() >= 480 && p.loggedIn() && !p.isAdmin()) { // 360000 (changed for more time)
				p.destroy(false);
			}
		} else if(inSeconds - p.getLastMoved() >= 300 && !p.isAdmin()) { // 5 mins
			if(p.isSleeping()) {
				p.setSleeping(false);
				p.getActionSender().sendWakeUp(false, false);
			}
			p.getActionSender().sendMessage("@cya@You have not moved for 5 mins, please move to a new area to avoid logout.");
			p.warnToMove();
		}
	}
	
	// Questionable
	private void sendQueuedPackets() {
		try {
			for (Player p : players) {
				if (p.destroyed()) {
					p.getSession().close();
					p.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

}
