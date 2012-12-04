package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

public interface InvUseOnItemListener {

    public void onInvUseOnItem(Player player, InvItem item1, InvItem item2);
}
