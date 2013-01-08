package com.prsc.gs.core;

import com.prsc.gs.event.DelayedEvent;



import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.util.Logger;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class DelayedEventHandler {
	
    private static World world = World.getWorld();
    
    private Queue<DelayedEvent> events = new ConcurrentLinkedQueue<DelayedEvent>();
    private Queue<DelayedEvent> toAdd = new ConcurrentLinkedQueue<DelayedEvent>();

    public DelayedEventHandler() {
        world.setDelayedEventHandler(this);
    }

    public void add(DelayedEvent event) {
    	if (!events.contains(event)) {
    		events.add(event);
    	}
    }
    
    public void doEvents() {
    	try {
    		Iterator<DelayedEvent> iterator = events.iterator();
            while (iterator.hasNext()) {
            	if(toAdd.size() < 0) {
            		events.addAll(toAdd);
            		toAdd.clear();
            	}
            	
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

    public boolean contains(DelayedEvent event) {
    	return events.contains(event);
    }

    public Queue<DelayedEvent> getEvents() {
    	return events;
    }

    public void remove(DelayedEvent event) {
    	events.remove(event);
    }

    public void removePlayersEvents(Player player) {
    	try {
    		Iterator<DelayedEvent> iterator = events.iterator();
    		while (iterator.hasNext()) {
    			DelayedEvent event = iterator.next();
    			if (event.belongsTo(player)) {
    				iterator.remove();
    			}
    		}
    	} catch (Exception e) {
    		Logger.println("Error @ removePlayer, IP address:" + player.getCurrentIP() + " Name: " + player.getUsername());
    		e.printStackTrace();
    	}
    }
}
