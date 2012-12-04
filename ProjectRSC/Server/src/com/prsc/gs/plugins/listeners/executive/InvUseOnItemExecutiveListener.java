package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

public interface InvUseOnItemExecutiveListener {

    public boolean blockInvUseOnItem(Player player, InvItem item1, InvItem item2);

}
