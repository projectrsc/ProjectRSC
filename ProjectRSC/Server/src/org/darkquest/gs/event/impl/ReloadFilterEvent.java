package org.darkquest.gs.event.impl;

import org.darkquest.gs.connection.filter.ConnectionFilter;
import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.model.Player;

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
