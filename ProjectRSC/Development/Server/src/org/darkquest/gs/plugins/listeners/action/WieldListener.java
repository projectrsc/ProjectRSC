package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

public interface WieldListener {

    public void onWield(Player player, InvItem item);
}
