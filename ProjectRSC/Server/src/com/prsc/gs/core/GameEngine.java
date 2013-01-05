package com.prsc.gs.core;

import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;

import org.python.google.common.collect.Lists;

import com.prsc.gs.Server;
import com.prsc.gs.connection.Client;
import com.prsc.gs.core.routine.Routine;
import com.prsc.gs.core.routine.impl.ClientProcessingRoutine;
import com.prsc.gs.core.routine.impl.LoginProcessRoutine;
import com.prsc.gs.core.routine.impl.NpcUpdateRoutine;
import com.prsc.gs.core.routine.impl.PlayerUpdateRoutine;
import com.prsc.gs.event.impl.DatabaseReconnectionEvent;
import com.prsc.gs.event.impl.ReloadFilterEvent;
import com.prsc.gs.event.impl.ResourceUsageUpdate;
import com.prsc.gs.event.impl.SaveProfileEvent;
import com.prsc.gs.event.impl.ShopRestockEvent;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Shop;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.util.Logger;

public final class GameEngine extends Thread {

    private static final World world = World.getWorld();
    
    private final List<Client> clients = new CopyOnWriteArrayList<Client>();

    private final DelayedEventHandler eventHandler = new DelayedEventHandler();
    
    private long lastSentClientUpdate = getAccurateTimestamp();
    
    private long lastSentClientUpdateFast = getAccurateTimestamp();
    
    private boolean running = true; 
    
    public void emptyWorld() {
        for (Player p : world.getPlayers()) {
            p.save();
            p.getActionSender().sendLogout();
        }
        World.getWorld().getServer().getLoginConnector().getActionSender().saveProfiles(false);
    }

    public void kill() {
        Logger.println("Terminating GameEngine");
        Server.getInstance().getTaskManager().getAvailableTaskWorkers().shutdown();
        running = false;
    }

    public void processEvents() {
    	eventHandler.doEvents();
    } 

    private void processLoginServer() {
        LoginConnector connector = World.getWorld().getServer().getLoginConnector();
        if (connector != null) {
            submitRoutine(new LoginProcessRoutine(Lists.newArrayList(connector.getPacketQueue())));
        }
    }
    
    public List<Client> getClients() {
    	return clients;
    }
    
    public void addClient(Client client) {
    	clients.add(client);
    }
    
    public void removeClient(Client client) {
    	clients.remove(client);
    }
    
    private void submitRoutine(Routine...routines) {
    	for(Routine routine : routines) {
    		Server.getInstance().getTaskManager().submitInternalRoutine(routine);
    	}
    }
    
    private void processIncomingSessions() {
    	submitRoutine(new ClientProcessingRoutine(clients));
    }
    
    private void processUpdate() {		
    	long now = getAccurateTimestamp();
		List<Player> players = Lists.newArrayList(World.getWorld().getPlayers());
		List<Npc> npcs = Lists.newArrayList(World.getWorld().getNpcs());
		
		long timeSinceMajor = now - lastSentClientUpdate;
		long timeSinceMinor = now - lastSentClientUpdateFast;
		
		if (timeSinceMajor >= 600) {
            lastSentClientUpdate = now;
            
            submitRoutine(new NpcUpdateRoutine(npcs, NpcUpdateRoutine.Task.POSITIONS));
            submitRoutine(new PlayerUpdateRoutine(players, PlayerUpdateRoutine.Task.POSITIONS));
            submitRoutine(new PlayerUpdateRoutine(players, PlayerUpdateRoutine.Task.MESSAGES));
            submitRoutine(new PlayerUpdateRoutine(players, PlayerUpdateRoutine.Task.OFFERS));
            submitRoutine(new PlayerUpdateRoutine(players, PlayerUpdateRoutine.Task.VIEWS));
            submitRoutine(new PlayerUpdateRoutine(players, PlayerUpdateRoutine.Task.COLLECTIONS));
            submitRoutine(new NpcUpdateRoutine(npcs, NpcUpdateRoutine.Task.COLLECTIONS));
		}
		
		if (timeSinceMinor >= 104) { // send queued packets?
            lastSentClientUpdateFast = now;
            
            submitRoutine(new PlayerUpdateRoutine(players, PlayerUpdateRoutine.Task.APPEARANCES));
            submitRoutine(new NpcUpdateRoutine(npcs, NpcUpdateRoutine.Task.APPEARANCES));
        } 
    }
    
    public static long getAccurateTimestamp() {
    	return System.nanoTime() / 1000000;
    }

    @Override
    public void run() {
        PluginHandler.getPluginHandler().handleAction("Startup", new Object[]{});

        eventHandler.add(new DatabaseReconnectionEvent());
        eventHandler.add(new SaveProfileEvent());
        eventHandler.add(new ReloadFilterEvent());
        eventHandler.add(new ResourceUsageUpdate());
        
        for (Shop shop : world.getShops()) {
			eventHandler.add(new ShopRestockEvent(shop));
		}
        
        while (running) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ie) {
            }
	        processIncomingSessions(); // threaded
	        processLoginServer(); // threaded
	        processEvents(); // TODO thread out
	        processUpdate(); // threaded
        } 
    }
}