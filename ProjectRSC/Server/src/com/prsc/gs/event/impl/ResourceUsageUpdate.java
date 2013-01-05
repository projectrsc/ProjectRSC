package com.prsc.gs.event.impl;

import com.prsc.gs.Server;
import com.prsc.gs.event.DelayedEvent;

public class ResourceUsageUpdate extends DelayedEvent {

	public ResourceUsageUpdate() {
		super(null, 30000);
	}

	@Override
	public void run() {
		long queued = Server.getInstance().getTaskManager().getFJPool().getQueuedTaskCount();
        System.out.println("[SERVER]: Queued tasks: " + queued);
        int wait  = Server.getInstance().getTaskManager().getFJPool().getRunningThreadCount();
        System.out.println("[SERVER]: Waiting to join threads: " + wait);
        int current  = Server.getInstance().getTaskManager().getFJPool().getActiveThreadCount();
        System.out.println("[SERVER]: Currently running threads: " + current);
	}
}
