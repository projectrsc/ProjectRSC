package com.prsc.gs.event.impl;

import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.model.Shop;


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
