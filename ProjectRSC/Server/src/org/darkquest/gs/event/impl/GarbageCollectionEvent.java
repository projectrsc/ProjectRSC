package org.darkquest.gs.event.impl;

import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.world.ActiveTile;
import org.darkquest.gs.world.World;

public final class GarbageCollectionEvent extends DelayedEvent {

    public GarbageCollectionEvent() {
        super(null, 6000000);
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            public void run() {
                collect();
            }
        }).start();
    }

    private synchronized void collect() {
        for (int i = 0; i < World.getWorld().tiles.length; i++) {
            for (int in = 0; in < World.getWorld().tiles[i].length; in++) {
                ActiveTile tile = World.getWorld().tiles[i][in];
                if (tile != null) {
                    if (!tile.hasGameObject() && !tile.hasItems() && !tile.hasNpcs() && !tile.hasPlayers()) {
                        World.getWorld().tiles[i][in] = null;
                    }
                }
            }
        }
        Runtime.getRuntime().gc();
    }

}
