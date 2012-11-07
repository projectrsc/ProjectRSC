package org.darkquest.ls;

import org.darkquest.ls.model.World;
import org.darkquest.ls.net.FPacket;
import org.darkquest.ls.net.LSPacket;
import org.darkquest.ls.net.PacketQueue;
import org.darkquest.ls.packethandler.PacketHandler;
import org.darkquest.ls.packethandler.PacketHandlerDef;
import org.darkquest.ls.util.PersistenceManager;

import java.util.List;
import java.util.TreeMap;


public final class LoginEngine extends Thread {
    /**
     * The mapping of packet IDs to their handler
     */
    private final TreeMap<Integer, PacketHandler> FPacketHandlers = new TreeMap<Integer, PacketHandler>();

    /**
     * The mapping of packet IDs to their handler
     */
    private final TreeMap<Integer, PacketHandler> LSPacketHandlers = new TreeMap<Integer, PacketHandler>();

    /**
     * The mapping of packet UIDs to their handler
     */
    private final TreeMap<Long, PacketHandler> uniqueHandlers = new TreeMap<Long, PacketHandler>();

    /**
     * The packet queue to be processed
     */
    private PacketQueue<FPacket> FPacketQueue;

    /**
     * The packet queue to be processed
     */
    private PacketQueue<LSPacket> LSPacketQueue;
    /**
     * Should we be running?
     */
    private boolean running = true;
    /**
     * The main server
     */
    private Server server;

    public LoginEngine(Server server) {
        this.server = server;
        LSPacketQueue = new PacketQueue<LSPacket>();
        FPacketQueue = new PacketQueue<FPacket>();
        loadPacketHandlers();
    }

    public PacketQueue<FPacket> getFPacketQueue() {
        return FPacketQueue;
    }

    public PacketQueue<LSPacket> getLSPacketQueue() {
        return LSPacketQueue;
    }

    /**
     * Loads the packet handling classes from the persistence manager.
     */
    protected void loadPacketHandlers() {
        //Loads packet handler configuration
        PacketHandlerDef[] handlerDefs = (PacketHandlerDef[]) PersistenceManager.load("LSPacketHandlers.xml");
        //Foreach packet handler defined
        for (PacketHandlerDef handlerDef : handlerDefs) {
            try {
                //Get the name of the class
                String className = handlerDef.getClassName();
                //Create a new instance
                Class<?> c = Class.forName(className);
                if (c != null) {
                    //Add handler to the list conf\ls
                    PacketHandler handler = (PacketHandler) c.newInstance();
                    for (int packetID : handlerDef.getAssociatedPackets()) {
                        LSPacketHandlers.put(packetID, handler);
                    }
                }
            } catch (Exception e) {
                Server.error(e);
            }
        }
        handlerDefs = (PacketHandlerDef[]) PersistenceManager.load("FPacketHandlers.xml");
        for (PacketHandlerDef handlerDef : handlerDefs) {
            try {
                String className = handlerDef.getClassName();
                Class<?> c = Class.forName(className);
                if (c != null) {
                    PacketHandler handler = (PacketHandler) c.newInstance();
                    for (int packetID : handlerDef.getAssociatedPackets()) {
                        FPacketHandlers.put(packetID, handler);
                    }
                }
            } catch (Exception e) {
                Server.error(e);
            }
        }
    }

    /**
     * Processes incoming packets.
     */
    private void processIncomingPackets() {
        for (LSPacket p : LSPacketQueue.getPackets()) {
            PacketHandler handler;
            if (((handler = uniqueHandlers.get(p.getUID())) != null) || ((handler = LSPacketHandlers.get(p.getID())) != null)) {
                try {
                    handler.handlePacket(p, p.getSession());
                    uniqueHandlers.remove(p.getUID());
                } catch (Exception e) {
                    Server.error("Exception with p[" + p.getID() + "]: " + e);
				e.printStackTrace();
                }
            } else {
                Server.error("Unhandled packet from server: " + p.getID());
            }
        }
        for (FPacket p : FPacketQueue.getPackets()) {
            PacketHandler handler = FPacketHandlers.get(p.getID());
            if (handler != null) {
                try {
                    handler.handlePacket(p, p.getSession());
                } catch (Exception e) {
                	e.printStackTrace();
                   // Server.error("Exception with p[" + p.getID() + "]: " + e);
                }
            } else {
            	
                System.out.println("Unhandled packet from frontend: " + p.getID());
            }
        }
    }

    /**
     * Processes outcoming packets.
     */
    public void processOutgoingPackets() {
        for (World w : server.getWorlds()) {
            List<LSPacket> packets = w.getActionSender().getPackets();
            for (LSPacket packet : packets) {
                w.getChannel().write(packet);
            }
            w.getActionSender().clearPackets();
        }
    }

    public void run() {
        while (running) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
            }
            processIncomingPackets();
            processOutgoingPackets();
        }
    }

    public void setHandler(long uID, PacketHandler handler) {
        uniqueHandlers.put(uID, handler);
    }
}
