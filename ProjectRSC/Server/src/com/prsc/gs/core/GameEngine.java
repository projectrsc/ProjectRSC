package com.prsc.gs.core;

import java.util.LinkedList;


import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.event.impl.DatabaseReconnectionEvent;
import com.prsc.gs.event.impl.GarbageCollectionEvent;
import com.prsc.gs.event.impl.ReloadFilterEvent;
import com.prsc.gs.event.impl.SaveProfileEvent;
import com.prsc.gs.event.impl.ShopRestockEvent;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.service.Services;
import com.prsc.gs.service.impl.PacketHandlers;
import com.prsc.gs.util.Logger;
import com.prsc.gs.world.Shop;
import com.prsc.gs.world.World;

public final class GameEngine extends Thread {

    private static final World world = World.getWorld();

    private final Map<Integer, PacketHandler> handlers = Services.lookup(PacketHandlers.class).getGameHandlers();

    private ClientUpdater clientUpdater = new ClientUpdater();

    private DelayedEventHandler eventHandler = new DelayedEventHandler();

    private List<RSCPacket> packetList = new LinkedList<RSCPacket>();
    
    private final Object packetListLock = new Object(); // Don't change.

    private long lastSentClientUpdate = System.currentTimeMillis();
    private long lastSentClientUpdateFast = System.currentTimeMillis();

    private boolean running = true;
    long time = 0;

    public void addPacket(RSCPacket packet) {
        // Add the packet to the packet list. NOTE: This must be done
        // in the synchronized block; do not change.
        synchronized (packetListLock) {
            packetList.add(packet);
        }
    }

    public void emptyWorld() {
        for (Player p : world.getPlayers()) {
            p.save();
            p.getActionSender().sendLogout();
        }
        World.getWorld().getServer().getLoginConnector().getActionSender().saveProfiles(false);
    }

    public void kill() {
        Logger.println("Terminating GameEngine");
        running = false;
    }

    private void processClients() {
        long now = System.currentTimeMillis();
        if (now - lastSentClientUpdate >= 600) {
            lastSentClientUpdate = now;
            clientUpdater.doMajor();
        }
        if (now - lastSentClientUpdateFast >= 104) {
            clientUpdater.sendQueuedPackets();

            lastSentClientUpdateFast = now;
            clientUpdater.doMinor();
        }
    }

    private void processEvents() {
        eventHandler.doEvents();
    }

    public void processLoginServer() {
        LoginConnector connector = World.getWorld().getServer().getLoginConnector();
        if (connector != null) {
            connector.processIncomingPackets();
            connector.sendQueuedPackets();
        }
    }

    public void processPackets() {
        // Get the list of packets that must be processed during this
        // game pulse. NOTE: This MUST be done inside the synchronized
        // block; do not change.
        List<RSCPacket> packets;
        synchronized (packetListLock) {
            packets = this.packetList;
            this.packetList = new LinkedList<RSCPacket>();
        }

        for (RSCPacket p : packets) {
            PacketHandler ph = handlers.get(p.getID());
            
            Player player = (Player) p.getSession().getAttachment();
            player.ping();
            if (ph != null) {
                try {
                    ph.handlePacket(p, p.getSession());
                } catch (Exception e) {
                    player.getActionSender().sendLogout();
                    player.destroy(false);
                    Logger.error(e);
                }
            } else {
                String err = String.format("Unhandled packet from %s: %d len: %d",
                        player.getCurrentIP(), p.getID(), p.getLength());

                Logger.error(err);
            }
        }
    }

    public void run() {
        time = System.currentTimeMillis();

        PluginHandler.getPluginHandler().handleAction("Startup", new Object[]{});

        eventHandler.add(new GarbageCollectionEvent());
        //eventHandler.add(new DatabaseReconnectionEvent());
        eventHandler.add(new SaveProfileEvent());
        eventHandler.add(new ReloadFilterEvent());
        
        for (Shop shop : world.getShops()) {
			eventHandler.add(new ShopRestockEvent(shop));
		}

        while (running) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ie) {
            }
            processPackets();
            processLoginServer();
            processEvents();
            processClients();
        }
    }

}