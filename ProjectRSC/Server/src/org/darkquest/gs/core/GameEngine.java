package org.darkquest.gs.core;

import java.util.LinkedList;


import java.util.List;
import java.util.Map;

import org.darkquest.gs.connection.RSCPacket;
import org.darkquest.gs.event.impl.DatabaseReconnectionEvent;
import org.darkquest.gs.event.impl.GarbageCollectionEvent;
import org.darkquest.gs.event.impl.ReloadFilterEvent;
import org.darkquest.gs.event.impl.SaveProfileEvent;
import org.darkquest.gs.event.impl.ShopRestockEvent;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.service.Services;
import org.darkquest.gs.service.impl.PacketHandlers;
import org.darkquest.gs.util.Logger;
import org.darkquest.gs.world.Shop;
import org.darkquest.gs.world.World;

public final class GameEngine extends Thread {

    private static final World world = World.getWorld();

    private final Map<Integer, PacketHandler> handlers = Services.lookup(PacketHandlers.class).getGameHandlers();

    private ClientUpdater clientUpdater = new ClientUpdater();

    private DelayedEventHandler eventHandler = new DelayedEventHandler();

    private List<RSCPacket> packetList = new LinkedList<RSCPacket>();
    
    private final Object packetListLock = new Object(); // Don't change.

    private long lastSentClientUpdate = System.nanoTime() / 1000000;
    private long lastSentClientUpdateFast = System.nanoTime() / 1000000;

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
        long now = System.nanoTime() / 1000000;
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
        time = System.nanoTime() / 1000000;

        PluginHandler.getPluginHandler().handleAction("Startup", new Object[]{});

        eventHandler.add(new GarbageCollectionEvent());
        eventHandler.add(new DatabaseReconnectionEvent());
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