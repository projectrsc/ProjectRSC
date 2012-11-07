package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Player;

public interface PlayerRangeListener {

    /**
     * Called when a player ranges another player
     */
    public void onPlayerRange(Player p, Player affectedMob);

}
