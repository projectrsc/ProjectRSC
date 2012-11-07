package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

public interface InvActionExecutiveListener {
    /**
     * Return true to prevent inventory action
     */
    public boolean blockInvAction(InvItem item, Player player);
}
