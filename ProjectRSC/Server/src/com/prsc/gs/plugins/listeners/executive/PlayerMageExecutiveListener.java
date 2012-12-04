package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Player;

public interface PlayerMageExecutiveListener {
    /**
     * Return true if you wish to prevent the cast
     *
     * @return
     */
    public boolean blockPlayerMage(Player player, Player affectedPlayer, int spell);
}
