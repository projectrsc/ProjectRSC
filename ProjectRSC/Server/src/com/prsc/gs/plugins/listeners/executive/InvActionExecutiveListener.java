package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

public interface InvActionExecutiveListener {
    /**
     * Return true to prevent inventory action
     */
    public boolean blockInvAction(InvItem item, Player player);
}
