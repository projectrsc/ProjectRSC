package com.prsc.gs.model;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.prsc.config.Constants;
import com.prsc.gs.Server;
import com.prsc.gs.core.DelayedEventHandler;
import com.prsc.gs.db.DBConnection;
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.event.SingleEvent;
import com.prsc.gs.external.GameObjectLoc;
import com.prsc.gs.external.NPCLoc;
import com.prsc.gs.io.WorldLoader;
import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Mob;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.component.world.TileValue;
import com.prsc.gs.plugins.QuestInterface;
import com.prsc.gs.states.CombatState;
import com.prsc.gs.util.EntityList;
import com.prsc.gs.util.Logger;

public final class World {
	
	private final List<QuestInterface> quests = new LinkedList<QuestInterface>();
	
	public static boolean PK_DAY = false;

	public static final int MAX_HEIGHT = 3776;

	public static final int MAX_WIDTH = 944;

	private static World worldInstance;

	public WorldLoader wl;

	private final DBConnection db = new DBConnection();

	public DBConnection getDB() {
		return db;
	}

	/**
	 * returns the only instance of this world, if there is not already one,
	 * makes it and loads everything
	 */
	public static synchronized World getWorld() {
		if (worldInstance == null) {
			worldInstance = new World();
			try {
				worldInstance.wl = new WorldLoader();
				try {
					Server.print("Loading Landscape", false);
					worldInstance.wl.loadWorld(worldInstance);
				} catch (Exception e) {
					Server.print("ERROR", true);
					e.printStackTrace();
				} finally {
					Server.print("COMPLETE", true);
				}

				try {
					Server.print("Loading Objects", false);
					worldInstance.getDB().loadObjects(worldInstance);
				} catch (Exception e) {
					Server.print("ERROR", true);
					e.printStackTrace();
				} finally {
					Server.print("COMPLETE", true);
				}
			} catch (Exception e) {
				Logger.error(e);
			}
		}
		return worldInstance;
	}

	private DelayedEventHandler delayedEventHandler;

	private final EntityList<Npc> npcs = new EntityList<Npc>(4000); 

	private final EntityList<Player> players = new EntityList<Player>(3000);

	private Server server;

	private final List<Shop> shops = new ArrayList<Shop>();

	private final TileValue[][] tileType = new TileValue[MAX_WIDTH][MAX_HEIGHT];

	public synchronized int countNpcs() {
		return npcs.size();
	}

	public synchronized int countPlayers() {
		return players.size();
	}
	
	public List<QuestInterface> getQuests() {
		return quests;
	}
	
	public void registerQuest(QuestInterface quest) {
		if (quest.getQuestName() == null) {
			throw new IllegalArgumentException("Quest name cannot be null");
		} else if (quest.getQuestName().length() > 40) {
			throw new IllegalArgumentException("Quest name cannot be longer then 40 characters");
		}
		for (QuestInterface q : quests) {
			if (q.getQuestId() == quest.getQuestId()) {
				throw new IllegalArgumentException("Quest ID must be unique");
			}
		}

		if (!Constants.GameServer.MEMBER_WORLD && quest.isMembers()) {
			return;
		}
		quests.add(quest);
	}
	
	public void unregisterQuest(QuestInterface quest) {
		if(quests.contains(quest)) {
			quests.remove(quest);
		}
	}

	public void sendWorldMessage(String msg) {
		synchronized (players) {
			for (Player p : players) {
				p.getActionSender().sendMessage(msg);
			}
		}
	}

	public void delayedRemoveObject(final GameObject object, final int delay) {
		delayedEventHandler.add(new SingleEvent(null, delay) {

			public void action() {
				unregisterGameObject(object);
				/*
				ActiveTile tile = getTile(object.getLocation());
				if (tile.hasGameObject() && tile.getGameObject().equals(object)) {
					unregisterGameObject(object);
				} */
			}
		});
	}

	/**
	 * Adds a DelayedEvent that will spawn a GameObject
	 */
	public void delayedSpawnObject(final GameObjectLoc loc, final int respawnTime) {
		delayedEventHandler.add(new SingleEvent(null, respawnTime) {

			public void action() {
				registerGameObject(new GameObject(loc));
			}
		}); 
	}
	
	public void delayedSpawnObject(final GameObjectLoc loc, final GameObject objToRemove, final int respawnTime) {
		delayedEventHandler.add(new SingleEvent(null, respawnTime) {

			public void action() {
				registerGameObject(new GameObject(loc));
				objToRemove.remove();
			}
		}); 
	}

	/**
	 * Gets the DelayedEventHandler instance
	 */
	public DelayedEventHandler getDelayedEventHandler() {
		return delayedEventHandler;
	}

	public Npc getNpcById(int id) {
		for (Npc npc : npcs) {
			if (npc.getID() == id) {
				return npc;
			}
		}
		return null;
	}
	
	public Npc getNpc(int idx) {
		return npcs.get(idx);
	}

	public Npc getNpc(int id, int minX, int maxX, int minY, int maxY) {
		for (Npc npc : npcs) {
			if (npc.getID() == id && npc.getX() >= minX && npc.getX() <= maxX && npc.getY() >= minY && npc.getY() <= maxY) {
				return npc;
			}
		}
		return null;
	}

	public Npc getNpc(int id, int minX, int maxX, int minY, int maxY, boolean notNull) {
		for (Npc npc : npcs) {
			if (npc.getID() == id && npc.getX() >= minX && npc.getX() <= maxX && npc.getY() >= minY && npc.getY() <= maxY) {
				if (!npc.inCombat()) {
					return npc;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the list of npcs on the server
	 */
	public synchronized EntityList<Npc> getNpcs() {
		return npcs;
	}

	/**
	 * Gets a Player by their server index
	 */
	public Player getPlayer(int idx) {
		try {
			Player p = players.get(idx);
			return p;
		} catch(Exception e) {
			return null;
		}
	
	}

	/**
	 * Gets a player by their username hash
	 */
	public Player getPlayer(long usernameHash) {
		for (Player p : players) {
			if (p.getUsernameHash() == usernameHash) {
				return p;
			}
		}
		return null;
	}

	public synchronized EntityList<Player> getPlayers() {
		return players;
	}

	public Server getServer() {
		return server;
	}

	public List<Shop> getShops() {
		return shops;
	}
	
	/*
	public ActiveTile getTile(int x, int y) {
		if (!withinWorld(x, y)) {
			return null;
		}
		ActiveTile t = tiles[x][y];
		if (t == null) {
			t = new ActiveTile(x, y);
			tiles[x][y] = t;
		}
		return t;
	}

	public ActiveTile getTile(Point p) {
		return getTile(p.getX(), p.getY());
	} */

	public TileValue getTileValue(int x, int y) {
		if (!withinWorld(x, y)) {
			return new TileValue();
		}
		TileValue t = tileType[x][y];
		if (t == null) {
			t = new TileValue();
			tileType[x][y] = t;
		}
		return t;
	}
	
	public boolean hasNpc(Npc n) {
		return npcs.contains(n);
	}

	public boolean hasPlayer(Player p) {
		return players.contains(p);
	}

	public boolean isLoggedIn(long usernameHash) {
		Player friend = getPlayer(usernameHash);
		if (friend != null) {
			return friend.loggedIn();
		}
		return false;
	}

	public void registerDoor(GameObject o) {
		if (o.getDoorDef().getDoorType() != 1) {
			return;
		}
		int dir = o.getDirection();
		int x = o.getX(), y = o.getY();
		if (dir == 0) {
			getTileValue(x, y).objectValue |= 1;
			getTileValue(x, y - 1).objectValue |= 4;
		} else if (dir == 1) {
			getTileValue(x, y).objectValue |= 2;
			getTileValue(x - 1, y).objectValue |= 8;
		} else if (dir == 2) {
			getTileValue(x, y).objectValue |= 0x10;
		} else if (dir == 3) {
			getTileValue(x, y).objectValue |= 0x20;
		}
	}

	public void registerGameObject(GameObject o) {
		switch (o.getType()) {
		case 0:
			registerObject(o);
			break;
		case 1:
			registerDoor(o);
			break;
		}
	}

	public void registerItem(final Item i) {
		try {
			if (i.getLoc() == null) {
				delayedEventHandler.add(new DelayedEvent(null, 180000) {

					public void run() {
						/*
						ActiveTile tile = getTile(i.getLocation());
						if (tile.hasItem(i)) {
							unregisterItem(i);
						} */
						unregisterItem(i);
						matchRunning = false;
					}
				});
			}
		} catch (Exception e) {
			i.remove();
			e.printStackTrace();
		}
	}
	
	public void registerNpc(Npc n) {
		NPCLoc npc = n.getLoc();
		if (npc.startX < npc.minX || npc.startX > npc.maxX || npc.startY < npc.minY || npc.startY > npc.maxY || (getTileValue(npc.startX, npc.startY).mapValue & 64) != 0) {
			// Logger.println("Fucked Npc: <id>" + npc.id + "</id><startX>" + npc.startX + "</startX><startY>" + npc.startY + "</startY>");
		}
		npcs.add(n);
	}

	public void registerObject(GameObject o) {
		if (o.getGameObjectDef().getType() != 1 && o.getGameObjectDef().getType() != 2) {
			return;
		}
		int dir = o.getDirection();
		int width, height;
		if (dir == 0 || dir == 4) {
			width = o.getGameObjectDef().getWidth();
			height = o.getGameObjectDef().getHeight();
		} else {
			height = o.getGameObjectDef().getWidth();
			width = o.getGameObjectDef().getHeight();
		}
		for (int x = o.getX(); x < o.getX() + width; x++) {
			for (int y = o.getY(); y < o.getY() + height; y++) {
				if (o.getGameObjectDef().getType() == 1) {
					getTileValue(x, y).objectValue |= 0x40;
				} else if (dir == 0) {
					getTileValue(x, y).objectValue |= 2;
					getTileValue(x - 1, y).objectValue |= 8;
				} else if (dir == 2) {
					getTileValue(x, y).objectValue |= 4;
					getTileValue(x, y + 1).objectValue |= 1;
				} else if (dir == 4) {
					getTileValue(x, y).objectValue |= 8;
					getTileValue(x + 1, y).objectValue |= 2;
				} else if (dir == 6) {
					getTileValue(x, y).objectValue |= 1;
					getTileValue(x, y - 1).objectValue |= 4;
				}
			}
		}

	}

	public void registerPlayer(Player p) {
		p.setInitialized();
		players.add(p);
	}

	public void registerShop(Shop shop) {
		shops.add(shop);
	}

	public void registerShops(Shop... shop) {
		shops.addAll(Arrays.asList(shop));
	}

	public void setDelayedEventHandler(DelayedEventHandler delayedEventHandler) {
		this.delayedEventHandler = delayedEventHandler;
	}

	/*
	public void setLocation(Entity entity, Point oldPoint, Point newPoint) {
		ActiveTile t;
		if (oldPoint != null) {
			t = getTile(oldPoint);
			t.remove(entity);
		}
		if (newPoint != null) {
			t = getTile(newPoint);
			t.add(entity);
		} 
	} */

	/**
	 * Sets the instance of the server
	 */
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Removes a door from the map
	 */
	public void unregisterDoor(GameObject o) {
		if (o.getDoorDef().getDoorType() != 1) {
			return;
		}
		int dir = o.getDirection();
		int x = o.getX(), y = o.getY();
		if (dir == 0) {
			getTileValue(x, y).objectValue &= 0xfffe;
			getTileValue(x, y - 1).objectValue &= 65535 - 4;
		} else if (dir == 1) {
			getTileValue(x, y).objectValue &= 0xfffd;
			getTileValue(x - 1, y).objectValue &= 65535 - 8;
		} else if (dir == 2) {
			getTileValue(x, y).objectValue &= 0xffef;
		} else if (dir == 3) {
			getTileValue(x, y).objectValue &= 0xffdf;
		}
	}

	/**
	 * Removes an object from the server
	 */
	public void unregisterGameObject(GameObject o) {
		o.remove();
		o.setLocation(o.getLocation());
		switch (o.getType()) {
		case 0:
			unregisterObject(o);
			break;
		case 1:
			unregisterDoor(o);
			break;
		}
	}

	/**
	 * Removes an item from the server
	 */
	public void unregisterItem(Item i) {
		i.remove();
		i.setLocation(i.getLocation());
	}

	/**
	 * Removes an npc from the server
	 */
	public void unregisterNpc(Npc n) {
		if (hasNpc(n)) {
			npcs.remove(n);
		}
		n.setLocation(n.getLocation());
	}

	/**
	 * Removes an object from the map
	 */
	public void unregisterObject(GameObject o) {
		if (o.getGameObjectDef().getType() != 1 && o.getGameObjectDef().getType() != 2) {
			return;
		}
		int dir = o.getDirection();
		int width, height;
		if (dir == 0 || dir == 4) {
			width = o.getGameObjectDef().getWidth();
			height = o.getGameObjectDef().getHeight();
		} else {
			height = o.getGameObjectDef().getWidth();
			width = o.getGameObjectDef().getHeight();
		}
		for (int x = o.getX(); x < o.getX() + width; x++) {
			for (int y = o.getY(); y < o.getY() + height; y++) {
				if (o.getGameObjectDef().getType() == 1) {
					getTileValue(x, y).objectValue &= 0xffbf;
				} else if (dir == 0) {
					getTileValue(x, y).objectValue &= 0xfffd;
					getTileValue(x - 1, y).objectValue &= 65535 - 8;
				} else if (dir == 2) {
					getTileValue(x, y).objectValue &= 0xfffb;
					getTileValue(x, y + 1).objectValue &= 65535 - 1;
				} else if (dir == 4) {
					getTileValue(x, y).objectValue &= 0xfff7;
					getTileValue(x + 1, y).objectValue &= 65535 - 2;
				} else if (dir == 6) {
					getTileValue(x, y).objectValue &= 0xfffe;
					getTileValue(x, y - 1).objectValue &= 65535 - 4;
				}
			}
		}
	}//System.out.println

	/**
	 * Removes a player from the server and saves their account
	 */
	public void unregisterPlayer(final Player p) {
		try {
			server.getLoginConnector().getActionSender().playerLogout(p.getUsernameHash());
			p.setLoggedIn(false);
			p.resetAll();
			p.save();
			Mob opponent = p.getOpponent();
			if (opponent != null) {
				p.resetCombat(CombatState.ERROR);
				opponent.resetCombat(CombatState.ERROR);
			}
			delayedEventHandler.removePlayersEvents(p);
			players.remove(p);
			p.setLocation(p.getLocation());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Are the given coords within the world boundaries
	 */
	public boolean withinWorld(int x, int y) {
		return x >= 0 && x < MAX_WIDTH && y >= 0 && y < MAX_HEIGHT;
	}

	/**
	 * Finds a specific quest by ID
	 *
	 * @param q
	 * @return
	 * @throws IllegalArgumentException when a quest by that ID isn't found
	 */
	public QuestInterface getQuest(int q) throws IllegalArgumentException {
		for (QuestInterface quest : this.getQuests()) {
			if (quest.getQuestId() == q) {
				return quest;
			}
		}
		throw new IllegalArgumentException("No quest found");
	}
}
