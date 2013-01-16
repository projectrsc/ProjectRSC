package com.prsc.gs.model;

import java.util.concurrent.atomic.AtomicReference;

import com.prsc.config.Constants;

import com.prsc.gs.core.GameEngine;
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.external.ItemDef;
import com.prsc.gs.external.ItemLoc;
import com.prsc.gs.model.component.world.Area;


public class Item extends Entity {

	/**
	 * Reference to the area this entity exists on
	 */
	private final AtomicReference<Area> area = new AtomicReference<Area>();
	
	/**
	 * Amount (for stackables)
	 */
	private int amount;
	/**
	 * Contains who dropped this item, if anyone
	 */
	public long droppedby = 0;

	/**
	 * Location definition of the item
	 */
	private ItemLoc loc = null;

	/**
	 * Contains the player that the item belongs to, if any
	 */
	private Player owner;

	/**
	 * Set when the item has been destroyed to alert players
	 */
	private boolean removed = false;
	/**
	 * The time that the item was spawned
	 */
	private long spawnedTime = 0L;

	public boolean holidayItem = false;

	public Item(int id, Point location) { //used for ::masks
		super.id = id;
		super.location = location;
		amount = 1;
		holidayItem = true;
	}
	
	public Item(int id, int x, int y, int amount, Player owner) {
		setID(id);
		setAmount(amount);
		this.owner = owner;
		if (owner != null)
			droppedby = owner.getUsernameHash();
		spawnedTime = GameEngine.getAccurateTimestamp();
		setLocation(Point.location(x, y));
	}

	public Item(int id, int x, int y, int amount, Player owner, long spawntime) {
		setID(id);
		setAmount(amount);
		this.owner = owner;
		if (owner != null)
			droppedby = owner.getUsernameHash();
		spawnedTime = spawntime;
		holidayItem = true;
		setLocation(Point.location(x, y));
	}

	public Item(ItemLoc loc) {
		this.loc = loc;
		setID(loc.id);
		setAmount(loc.amount);
		spawnedTime = GameEngine.getAccurateTimestamp();
		setLocation(Point.location(loc.x, loc.y));
	}

	public long droppedby() {
		return droppedby;
	}

	public boolean equals(Object o) {
		if (o instanceof Item) {
			Item item = (Item) o;
			return item.getID() == getID() && item.getAmount() == getAmount() && item.getSpawnedTime() == getSpawnedTime() && (item.getOwner() == null || item.getOwner().equals(getOwner())) && item.getLocation().equals(getLocation());
		}
		return false;
	}

	public int getAmount() {
		return amount;
	}

	public ItemDef getDef() {
		return EntityHandler.getItemDef(id);
	}

	public ItemLoc getLoc() {
		return loc;
	}

	public Player getOwner() {
		return owner;
	}

	public long getSpawnedTime() {
		return spawnedTime;
	}

	public boolean isOn(int x, int y) {
		return x == getX() && y == getY();
	}

	public boolean isRemoved() {
		return removed;
	}

	public void remove() {
		Area curRegion = area.get();
		curRegion.removeItem(this);
		
		if (!removed && loc != null && loc.getRespawnTime() > 0) {
			World.getWorld().getDelayedEventHandler().add(new DelayedEvent(null, loc.getRespawnTime() * 1000) {
				public void run() {
					world.registerItem(new Item(loc));
					matchRunning = false;
				}
			});
		}
		removed = true;
	}

	public void setAmount(int amount) {
		if(getDef() != null) {
			if (getDef().isStackable()) {
				this.amount = amount;
			} else {
				this.amount = 1;
			}
		}
	}

	public void setdroppedby(long hash) {
		droppedby = hash;
	}

	public boolean visibleTo(Player p) {
	try {
		if (!holidayItem && (owner == null || p.equals(owner))) {
			return true;
		}
		if (getDef().isMembers() && !Constants.GameServer.MEMBER_WORLD) {
			return false;
		}
		if (!getDef().canTrade())
			return false;

	} catch (Exception e) {
		System.out.println("Player " + p.getUsername() + " caused exception \n" + e);
        }
		return GameEngine.getAccurateTimestamp() - spawnedTime > 60000;
	}
	
	@Override
	public void setLocation(Point p) {
		Area r = Area.getArea(p);
        Area cur = area.get();

        if (cur != r) {
            if (cur != null) {
                cur.removeItem(this);
            }
            r.addItem(this);
            area.getAndSet(r);
        }
		super.setLocation(p);
	}
	
	@Override
	public String toString() {
		return "Item(" + this.id + ", " + this.amount + ")";
	}
}
