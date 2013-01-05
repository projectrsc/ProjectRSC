package com.prsc.gs.core.routine.impl;


import java.util.ArrayList;
import java.util.List;

import org.python.google.common.collect.Lists;

import com.prsc.gs.core.TaskManager;
import com.prsc.gs.core.routine.Routine;
import com.prsc.gs.model.Npc;

public class NpcUpdateRoutine extends Routine {
	
	private List<Npc> npcs = null;
	private int numWorkers = TaskManager.WORKER_COUNT;
	private Task task = null;
	
	public NpcUpdateRoutine(List<Npc> npcs, Task task) {
		this.npcs = npcs;
		this.task = task;
	}
	
	public enum Task {
		POSITIONS, COLLECTIONS, APPEARANCES
	}
	
	private void computeDirectly() {
		switch(task) {
		case POSITIONS:
			updateNpcPositions();
			break;
		case COLLECTIONS:
			updateCollections();
			break;
		case APPEARANCES:
			updateAppearances();
			break;
		}
	}

	@Override
	protected void compute() {
		int maxWorkers = numWorkers * 16; 
		
		if(npcs.isEmpty()) { 
			return;
		}
		
		if(npcs.size() < maxWorkers) {
			computeDirectly();
			return;
		}
		
		int split = npcs.size() / numWorkers;
		
		List<NpcUpdateRoutine> tasks = new ArrayList<NpcUpdateRoutine>();
		List<List<Npc>> o = Lists.partition(npcs, split);
		
		for(List<Npc> sub : o) {
			tasks.add(new NpcUpdateRoutine(sub, task));
		}
		
		invokeAll(tasks);
	}
	
	private void updateNpcPositions() {
		for(Npc n : npcs) {
			n.resetMoved();
			n.updatePosition();
			n.updateAppearanceID();
		}
	}
	
	private void updateCollections() {
		for (Npc n : npcs) {
			n.resetSpriteChanged();
		}
	}
	
	private void updateAppearances() {
		for (Npc n : npcs) {
			n.setAppearnceChanged(false);
		}
	}

}
