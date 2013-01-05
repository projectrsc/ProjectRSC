package com.prsc.gs.core.routine.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.python.google.common.collect.Lists;

import com.prsc.gs.core.GameEngine;
import com.prsc.gs.core.routine.Routine;
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.util.Logger;

public class EventProcessingRoutine extends Routine {
	
	private List<DelayedEvent> events = null;
	private int numWorkers = 0;
	
	public EventProcessingRoutine(List<DelayedEvent> events, int numWorkers) {
		this.events = events;
		this.numWorkers = numWorkers;
	}
	
	protected void computeNow() {
		try {
            Iterator<DelayedEvent> iterator = events.iterator();
            while (iterator.hasNext()) {
                DelayedEvent event = iterator.next();

                if (event == null) {
                    iterator.remove();
                    continue;
                }

                if (event.shouldRun()) {
                    event.run();
                    event.updateLastRun();
                }
                if (event.shouldRemove()) {
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.println("Error @ doEvents(): " + e);
        }
	}

	@Override
	protected void compute() {
		int maxWorkers = numWorkers * 16;
		
		if(events.isEmpty()) {
			return;
		}
		
		if(events.size() < maxWorkers) {
			computeNow();
			return;
		}
		
		int split = events.size() / numWorkers;
		List<EventProcessingRoutine> tasks = new ArrayList<EventProcessingRoutine>();
		List<List<DelayedEvent>> partitionedEvents = Lists.partition(events, split);
		for(List<DelayedEvent> events : partitionedEvents) {
			tasks.add(new EventProcessingRoutine(events, numWorkers));
		}
		invokeAll(tasks);
	}

}
