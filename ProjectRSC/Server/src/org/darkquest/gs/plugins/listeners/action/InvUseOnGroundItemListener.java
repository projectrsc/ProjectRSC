package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.Player;

public interface InvUseOnGroundItemListener {

	public void onInvUseOnGroundItem(InvItem myItem, Item item, Player player);

}
