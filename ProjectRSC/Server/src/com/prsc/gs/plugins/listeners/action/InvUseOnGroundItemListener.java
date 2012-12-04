package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Player;

public interface InvUseOnGroundItemListener {

	public void onInvUseOnGroundItem(InvItem myItem, Item item, Player player);

}
