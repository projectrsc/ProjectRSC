package org.darkquest.gs.event.impl;

import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.world.World;

public final class SaveProfileEvent extends DelayedEvent {

    public SaveProfileEvent() {
        super(null, 300000);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        for (Player p : world.getPlayers()) {
            if (now - p.getLastSaveTime() >= 900000) {
                p.save();
                p.setLastSaveTime(now);
            }
        }
        World.getWorld().getServer().getLoginConnector().getActionSender().saveProfiles();
    }

}
