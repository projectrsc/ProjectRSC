package com.prsc.gs.event.impl;

import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.world.World;

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
