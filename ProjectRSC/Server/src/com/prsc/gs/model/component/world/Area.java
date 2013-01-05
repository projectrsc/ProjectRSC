package com.prsc.gs.model.component.world;

import java.util.Collections;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Mob;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Point;
import com.prsc.gs.model.World;

/**
 * Region - renamed as Area
 * @author Lothy
 * 
 */

public final class Area {

    private static final int AREA_SIZE = 40;

    private static final int LOWER_BOUND = (AREA_SIZE / 2) - 1;

    private static final int HORIZONTAL_PLANES = (World.MAX_WIDTH / AREA_SIZE) + 1;

    private static final int VERTICAL_PLANES = (World.MAX_HEIGHT / AREA_SIZE) + 1;

    private static final Area[][] areas = new Area[HORIZONTAL_PLANES][VERTICAL_PLANES];

    private final Queue<GameObject> objects = new ConcurrentLinkedQueue<GameObject>();

    private final Queue<Item> items = new ConcurrentLinkedQueue<Item>();

    private final Queue<Npc> npcs = new ConcurrentLinkedQueue<Npc>();

    private final Queue<Player> players = new ConcurrentLinkedQueue<Player>();
    
    static {
        for (int x = 0; x < HORIZONTAL_PLANES; x++) {
            for (int y = 0; y < VERTICAL_PLANES; y++) {
                areas[x][y] = new Area();
            }
        }
    }

    public static Area getArea(Point p) {
        return getArea(p.getX(), p.getY());
    }

    private static Area getArea(int x, int y) {
        int areaX = x / AREA_SIZE;
        int areaY = y / AREA_SIZE;

        return areas[areaX][areaY];
    }

    public static Area[] getViewableAreas(Point p) {
        return getViewableAreas(p.getX(), p.getY());
    }

    private static Area[] getViewableAreas(int x, int y) {
        Area[] neighbours = new Area[4];
        int areaX = x / AREA_SIZE;
        int areaY = y / AREA_SIZE;
        neighbours[0] = areas[areaX][areaY];

        int relX = x % AREA_SIZE;
        int relY = y % AREA_SIZE;

        if (relX <= LOWER_BOUND) {
            if (relY <= LOWER_BOUND) {
                neighbours[1] = areas[areaX - 1][areaY];
                neighbours[2] = areas[areaX - 1][areaY - 1];
                neighbours[3] = areas[areaX][areaY - 1];
            } else {
                neighbours[1] = areas[areaX - 1][areaY];
                neighbours[2] = areas[areaX - 1][areaY + 1];
                neighbours[3] = areas[areaX][areaY + 1];
            }
        } else {
            if (relY <= LOWER_BOUND) {
                neighbours[1] = areas[areaX + 1][areaY];
                neighbours[2] = areas[areaX + 1][areaY - 1];
                neighbours[3] = areas[areaX][areaY - 1];
            } else {
                neighbours[1] = areas[areaX + 1][areaY];
                neighbours[2] = areas[areaX + 1][areaY + 1];
                neighbours[3] = areas[areaX][areaY + 1];
            }
        }

        return neighbours;
    }

    public static Iterable<GameObject> getViewableObjects(Point p, int radius) {
        Area[] areas = getViewableAreas(p);
        List<GameObject> objects = new LinkedList<GameObject>();

        for (Area r : areas) {
            for (GameObject go : r.getObjects()) {
                if (!go.isRemoved() && p.withinRange(go.getLocation(), radius)) {
                    objects.add(go);
                }
            }
        }

        return objects;
    }

    public static Iterable<Item> getViewableItems(Point p, int radius) {
        Area[] areas = getViewableAreas(p);
        List<Item> items = new LinkedList<Item>();

        for (Area r : areas) {
            for (Item i : r.getItems()) {
                if (!i.isRemoved() && p.withinRange(i.getLocation(), radius)) { // ()
                    items.add(i);
                }
            }
        }

        return items;
    }

    public static Iterable<Npc> getViewableNpcs(Point p, int radius) {
        Area[] areas = getViewableAreas(p);
        List<Npc> npcs = new LinkedList<Npc>();

        for (Area r : areas) {
            for (Npc n : r.getNpcs()) {
                if (p.withinRange(n.getLocation(), radius)) {
                    npcs.add(n);
                }
            }
        }

        return npcs;
    }

    public static Iterable<Player> getViewablePlayers(Point p, int radius) {
        Area[] areas = getViewableAreas(p);
        List<Player> players = new LinkedList<Player>();

        for (Area r : areas) {
            for (Player player : r.getPlayers()) {
                if (player.isLoggedIn() && p.withinRange(player.getLocation(), radius)) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public static Iterable<Player> getViewablePlayers(Player player, int radius) {
        Point loc = player.getLocation();
        Area[] areas = getViewableAreas(loc);
        List<Player> players = new LinkedList<Player>();

        for (Area r : areas) {
            for (Player p : r.getPlayers()) {
                if (p != player && p.isLoggedIn() && loc.withinRange(p.getLocation(), radius)) {
                    players.add(p);
                }
            }
        }

        return players;
    }

    public Iterable<GameObject> getObjects() {
        return Collections.unmodifiableCollection(objects);
    }

    public Iterable<Item> getItems() {
        return Collections.unmodifiableCollection(items);
    }

    public Iterable<Npc> getNpcs() {
        return Collections.unmodifiableCollection(npcs);
    }

    public Iterable<Player> getPlayers() {
        return Collections.unmodifiableCollection(players);
    }

    public GameObject getObject(int x, int y) {
    	for(GameObject o : objects) {
    		if(o.getX() == x && o.getY() == y) {
    			return o;
    		}
    	}
    	return null;
    }

    public Item getItem(int itemId, Point location) {
        for (Item i : items) {
            if (i.getID() == itemId && i.getLocation().equals(location)) {
                return i;
            }
        }

        return null;
    }

    public Npc getNpc() {
        throw new UnsupportedOperationException();
    }

    public Player getPlayer() {
        throw new UnsupportedOperationException();
    }

    public void addObject(GameObject go) {
        objects.add(go);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addNpc(Npc npc) {
        npcs.add(npc);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
    
    public void addPlayer(Mob mob) {
    	players.add((Player)mob);
    }

    public void removeObject(GameObject go) {
        objects.remove(go);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void removeNpc(Npc npc) {
        npcs.remove(npc);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }
    
    public void removePlayer(Mob mob) {
    	players.remove((Player)mob);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(2000);
        sb.append("Players:\n");
        for (Player p : players) {
            sb.append("\t").append(p).append("\n");
        }
        
        sb.append("\nNpcs:\n");
        for (Npc n : npcs) {
            sb.append("\t").append(n).append("\n");
        }

        sb.append("\nItems:\n");
        for (Item i : items) {
            sb.append("\t").append(i).append("\n");
        }
        
        sb.append("\nObjects:\n");
        for (Object o : objects) {
            sb.append("\t").append(o).append("\n");
        }

        return sb.toString();
    }
}
