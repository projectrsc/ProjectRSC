package com.prsc.gs.event;

import com.prsc.gs.core.DelayedEventHandler;
import com.prsc.gs.core.GameEngine;

import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;


public abstract class DelayedEvent {

    public static final World world = World.getWorld();
    protected int delay = 500;
    protected final DelayedEventHandler handler = World.getWorld().getDelayedEventHandler();
    private long lastRun = GameEngine.getAccurateTimestamp(); //System.currentTimeMillis();
    protected Player owner;
    protected boolean matchRunning = true;

    public DelayedEvent(Player owner, int delay) {
        this.owner = owner;
        this.delay = delay;
    }

    public boolean belongsTo(Player player) {
        return owner != null && owner.equals(player);
    }

    public int getDelay() {
        return delay;
    }

    public Object getIdentifier() {
        return null;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean is(DelayedEvent e) {
        return (e.getIdentifier() != null && e.getIdentifier().equals(getIdentifier()));
    }

    public abstract void run();

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setLastRun(long time) {
        lastRun = time;
    }

    public final boolean shouldRemove() {
        return !matchRunning;
    }

    public final boolean shouldRun() {
        return matchRunning && GameEngine.getAccurateTimestamp() - lastRun >= delay;
    }

    public final void stop() {
        matchRunning = false;
    }

    public int timeTillNextRun() {
        int time = (int) (delay - (GameEngine.getAccurateTimestamp() - lastRun));
        return time < 0 ? 0 : time;
    }

    public final void updateLastRun() {
        lastRun = GameEngine.getAccurateTimestamp(); //System.currentTimeMillis();
    }
}
