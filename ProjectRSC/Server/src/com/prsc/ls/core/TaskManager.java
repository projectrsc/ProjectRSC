package com.prsc.ls.core;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

public class TaskManager {
	
	private final ExecutorService nettyBosses = Executors.newSingleThreadExecutor();
	private final ExecutorService taskWorkers = Executors.newCachedThreadPool();;
	
	public final ExecutorService getNettyBosses() {
		return nettyBosses;
	}
	
	public final ExecutorService getAvailableTaskWorkers() {
		return taskWorkers;
	}

}
