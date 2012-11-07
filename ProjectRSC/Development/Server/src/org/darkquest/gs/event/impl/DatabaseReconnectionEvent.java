package org.darkquest.gs.event.impl;

import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.world.World;

public final class DatabaseReconnectionEvent extends DelayedEvent {

    public DatabaseReconnectionEvent() {
        super(null, 1800000);
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            public void run() {
                World.getWorld().getDB().createConnection();
            }
        }).start();
    }

}
