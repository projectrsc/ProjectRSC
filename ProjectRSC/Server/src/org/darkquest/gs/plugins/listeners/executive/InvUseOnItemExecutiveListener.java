package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Player;

public interface InvUseOnItemExecutiveListener {

    public boolean blockInvUseOnItem(Player player, InvItem item1, InvItem item2);

}
