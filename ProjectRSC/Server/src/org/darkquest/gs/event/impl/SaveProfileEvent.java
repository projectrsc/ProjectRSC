package org.darkquest.gs.event.impl;

import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.util.Logger;
import org.darkquest.gs.world.World;

public final class SaveProfileEvent extends DelayedEvent {
	
	public static final int SAVE_TIME = 300000; // 5 mins 300000

    public SaveProfileEvent() {
        super(null, SAVE_TIME);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        Logger.print("***********************************************");
        Logger.print("******** SAVING ALL PLAYERS (AutoSave) **********");
        Logger.print("***********************************************");
        for (Player p : world.getPlayers()) {
            if (now - p.getLastSaveTime() >= SAVE_TIME) {
                p.save();
                p.setLastSaveTime(now);
            }
        }
        World.getWorld().getServer().getLoginConnector().getActionSender().saveProfiles(false);
    }

}
