package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;

public interface WieldListener {

    public void onWield(Player player, InvItem item);
}
