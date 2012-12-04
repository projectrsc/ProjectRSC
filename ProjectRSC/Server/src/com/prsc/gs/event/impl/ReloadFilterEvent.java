package com.prsc.gs.event.impl;


import com.prsc.gs.connection.filter.ConnectionFilter;
import com.prsc.gs.event.DelayedEvent;

public class ReloadFilterEvent extends DelayedEvent {

	public ReloadFilterEvent() {
		super(null, 300000); // every five mins
	}

	@Override
	public void run() {
		if(ConnectionFilter.getInstance() != null) {
			System.out.println("Flushing blacklist");
			ConnectionFilter.getInstance().clearBlacklist();
		}
	}

}
