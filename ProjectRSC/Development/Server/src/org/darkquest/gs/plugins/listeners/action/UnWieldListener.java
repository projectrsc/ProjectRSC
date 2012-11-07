package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

public interface UnWieldListener {

    public void onUnWield(Player player, InvItem item);

}
