package com.prsc.gs.event.impl;

import com.prsc.gs.core.GameEngine;
import com.prsc.gs.event.DelayedEvent;

import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.util.Logger;

public final class SaveProfileEvent extends DelayedEvent {
	
	public static final int SAVE_TIME = 300000; // 5 mins 300000

    public SaveProfileEvent() {
        super(null, SAVE_TIME);
    }

    @Override
    public void run() {
        long now = GameEngine.getAccurateTimestamp();
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
