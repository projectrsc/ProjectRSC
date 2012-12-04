package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

public interface PlayerRangeListener {

    /**
     * Called when a player ranges another player
     */
    public void onPlayerRange(Player p, Player affectedMob);

}
