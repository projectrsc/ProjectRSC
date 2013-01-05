package com.prsc.gs.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Executors;

import com.prsc.gs.core.routine.Routine;
import com.prsc.gs.plugins.task.Task;

public class TaskManager {
	
	private final ExecutorService nettyBoss = Executors.newSingleThreadExecutor();
	private final ForkJoinPool taskWorkers = new ForkJoinPool();
	
	private ExecutorService nettyWorkers = null;
	
	public static int WORKER_COUNT = 0;
	
	public TaskManager(int initialWorkerCount) {
		this.nettyWorkers = Executors.newFixedThreadPool(initialWorkerCount);
		WORKER_COUNT = initialWorkerCount;
	}
	
	public ExecutorService getAvailableTaskWorkers() {
		return taskWorkers;
	}
	
	public ExecutorService getAvailableNettyBosses() {
		return nettyBoss;
	}
	
	public ExecutorService getAvailableNettyWorkers() {
		return nettyWorkers;
	}
	
	public ForkJoinPool getFJPool() {
		return taskWorkers;
	}
	
	public void submitInternalRoutine(Routine routine) {
		taskWorkers.invoke(routine);
	}
	
	public void submitTask(Task task) {
		taskWorkers.submit(task);
	}

}
