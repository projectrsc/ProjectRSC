package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Player;

public interface PlayerMageListener {
    /**
     * Called when you mage a Player
     */
    public void onPlayerMage(Player player, Player affectedPlayer, int spell);
}
