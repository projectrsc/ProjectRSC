package com.prsc.gs.core;

import java.util.ArrayList;



import java.util.ConcurrentModificationException;

import com.prsc.gs.builders.GameObjectPositionPacketBuilder;
import com.prsc.gs.builders.ItemPositionPacketBuilder;
import com.prsc.gs.builders.NpcPositionPacketBuilder;
import com.prsc.gs.builders.NpcUpdatePacketBuilder;
import com.prsc.gs.builders.PlayerPositionPacketBuilder;
import com.prsc.gs.builders.PlayerUpdatePacketBuilder;
import com.prsc.gs.builders.WallObjectPositionPacketBuilder;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.model.ChatMessage;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.tools.DataConversions;
import com.prsc.gs.util.EntityList;

/**
 * @TODO
 * Segment routines into workers available by assigning workers regions 
 * needing to be processed 
 * @author openfrog
 */

public final class ClientUpdater {

	//private final EntityList<Player> players = World.getWorld().getPlayers();

	//private final EntityList<Npc> npcs = World.getWorld().getNpcs();
	/*
	private final GameObjectPositionPacketBuilder gameObjectPositionBuilder = new GameObjectPositionPacketBuilder();

	private final ItemPositionPacketBuilder itemPositionBuilder = new ItemPositionPacketBuilder();

	private final NpcUpdatePacketBuilder npcApperanceBuilder = new NpcUpdatePacketBuilder();

	private final NpcPositionPacketBuilder npcPositionPacketBuilder = new NpcPositionPacketBuilder();

	private final PlayerUpdatePacketBuilder playerApperanceBuilder = new PlayerUpdatePacketBuilder();

	private final PlayerPositionPacketBuilder playerPositionBuilder = new PlayerPositionPacketBuilder();

	private final WallObjectPositionPacketBuilder wallObjectPositionPacketBuilder = new WallObjectPositionPacketBuilder();
	*/
	/**
	 * Sends queued packets to each player
	 */
	/*
	public void sendQueuedPackets() {
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
	} */

	/**
	 * Update player/npc appearances, game objects, items, wall objects, ping
	 */
	public void doMinor() {
		/*
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
		synchronized (npcs) {
			for (Npc n : npcs) {
				n.setAppearnceChanged(false);
			}
		} */
	}

	//public volatile boolean updatingCollections;

	public void doMajor() {
		//updateNpcPositions();
		//updatePlayersPositions();
		//updateMessageQueues();
		//updateOffers();
		
		/*
		for (Player p : players) { // sending update packets
			updateTimeouts(p);

			updatePlayerPositions(p);
			updateNpcPositions(p);
			updateGameObjects(p);
			updateWallObjects(p);
			updateItems(p);

			p.setFirstMajorUpdateSent(true);
		} */

		//updateCollections();
	} 
	
	/*
	public void process(Player p) {
		updateTimeouts(p);

		updatePlayerPositions(p); // Must be done before updating any objects/items/npcs!
		updateNpcPositions(p);
		updateGameObjects(p);
		updateWallObjects(p);
		updateItems(p);

		p.setFirstMajorUpdateSent(true);
	} */

	/**
	 * Updates collections, new becomes known, removing is removed etc.
	 */
	/*
	public void updateCollections() {
		updatingCollections = true;
		
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

		for (Npc n : npcs) {
			n.resetSpriteChanged();
		}

		updatingCollections = false;
	} */

	/**
	 * Sends updates for game objects to the given player
	 */
	/*
	private void updateGameObjects(Player p) {
		gameObjectPositionBuilder.setPlayer(p);
		RSCPacket temp = gameObjectPositionBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	} */

	/**
	 * Sends updates for game items to the given player
	 */
	/*
	private void updateItems(Player p) {
		itemPositionBuilder.setPlayer(p);
		RSCPacket temp = itemPositionBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	} */

	/**
	 * Updates the messages queues for each player
	 */
	/*
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
	} */

	/**
	 * Update appearance of any npcs the given player should be aware of
	 */
	/*
	private void updateNpcApperances(Player p) {
		npcApperanceBuilder.setPlayer(p);
		RSCPacket temp = npcApperanceBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	} */

	/**
	 * Update the position of npcs, and check if who (and what) they are aware
	 * of needs updated
	 */
	/*
	private void updateNpcPositions() {
		for(Npc n : npcs) {
			n.resetMoved();
			n.updatePosition();
			n.updateAppearanceID();
		}
	} */
	
	/**
	 * Update the position of players, and check if who (and what) they are
	 * aware of needs updated
	 */
	/*
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
	} */

	/**
	 * Sends updates for npcs to the given player
	 */
	/*
	private void updateNpcPositions(Player p) {
		npcPositionPacketBuilder.setPlayer(p);
		RSCPacket temp = npcPositionPacketBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	} */
	
	/*
	public void updateOffers() {
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
			} else if (player.isDueling()) {
				Player affectedPlayer = player.getWishToDuel();
				if (affectedPlayer == null) {
					continue;
				}
				player.getActionSender().sendDuelSettingUpdate();
				affectedPlayer.getActionSender().sendDuelSettingUpdate();
				affectedPlayer.getActionSender().sendDuelItems();
			}
		}
	} */

	/**
	 * Update appearance of the given player, and any players they should be
	 * aware of
	 */
	/*
	private void updatePlayerApperances(Player p) {
		try {
			playerApperanceBuilder.setPlayer(p);
			RSCPacket temp = playerApperanceBuilder.getPacket();
			if (temp != null) {
				p.getSession().write(temp);
			}
		} catch(Exception e) {
			if(!(e instanceof ConcurrentModificationException))
				e.printStackTrace();
		}
	} */

	/**
	 * Update positions of the given player, and any players they should be
	 * aware of
	 */
	/*
	private void updatePlayerPositions(Player p) {
		playerPositionBuilder.setPlayer(p);
		RSCPacket temp = playerPositionBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	} */

	/**
	 * Checks the player has moved within the last 5mins
	 */
	/*
	private void updateTimeouts(Player p) {
		if (p.destroyed()) {
			return;
		}
		long curTime = System.currentTimeMillis();

		if (curTime - p.getLastPing() >= 30000) {
			p.destroy(false); // was false - xent
		} else if (curTime - p.getLastPing() >= 60000) { // 1 minute force logout
			p.destroy(true);
		} else if(p.warnedToMove()) {
			if (curTime - p.getLastMoved() >= 480000 && p.loggedIn() && !p.isAdmin()) { // 360000 (changed for more time)
				p.destroy(false);
			}
		} else if (curTime - p.getLastMoved() >= 300000 && !p.isAdmin()) { // 300000
			p.getActionSender().sendMessage("@cya@You have not moved for 5 mins, please move to a new area to avoid logout.");
			p.warnToMove();
		}
	} */

	/**
	 * Sends updates for wall objects to the given player
	 */
	/*
	private void updateWallObjects(Player p) {
		wallObjectPositionPacketBuilder.setPlayer(p);
		RSCPacket temp = wallObjectPositionPacketBuilder.getPacket();
		if (temp != null) {
			p.getSession().write(temp);
		}
	} */
}
