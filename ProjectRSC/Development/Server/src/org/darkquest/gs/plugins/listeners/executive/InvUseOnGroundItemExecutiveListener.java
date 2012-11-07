package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.Player;

public interface InvUseOnGroundItemExecutiveListener {

    public boolean blockInvUseOnGroundItem(InvItem myItem, Item item, Player player);

}
