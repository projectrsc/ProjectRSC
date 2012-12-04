package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Player;

public interface InvUseOnGroundItemExecutiveListener {

    public boolean blockInvUseOnGroundItem(InvItem myItem, Item item, Player player);

}
