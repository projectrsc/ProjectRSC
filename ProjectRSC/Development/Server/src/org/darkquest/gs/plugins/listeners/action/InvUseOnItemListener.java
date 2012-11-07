package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

public interface InvUseOnItemListener {

    public void onInvUseOnItem(Player player, InvItem item1, InvItem item2);
}
