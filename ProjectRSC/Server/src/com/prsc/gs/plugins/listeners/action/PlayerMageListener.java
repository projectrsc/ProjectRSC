package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

public interface PlayerMageListener {
    /**
     * Called when you mage a Player
     */
    public void onPlayerMage(Player player, Player affectedPlayer, int spell);
}
