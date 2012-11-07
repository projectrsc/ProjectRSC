package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Player;

public interface PlayerMageExecutiveListener {
    /**
     * Return true if you wish to prevent the cast
     *
     * @return
     */
    public boolean blockPlayerMage(Player player, Player affectedPlayer, int spell);
}
