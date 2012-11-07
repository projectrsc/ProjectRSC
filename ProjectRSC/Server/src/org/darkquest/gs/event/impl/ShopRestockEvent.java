package org.darkquest.gs.event.impl;

import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.world.Shop;

public final class ShopRestockEvent extends DelayedEvent {

	private final Shop shop;

	public ShopRestockEvent(Shop shop) {
		super(null, shop.getRespawnRate());
		this.shop = shop;
	}

	@Override
	public void run() {
		shop.restock();
	}

}
