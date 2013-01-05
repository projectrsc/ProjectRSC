package com.prsc.ls.core;

import com.prsc.ls.Server;


import com.prsc.ls.net.FPacket;
import com.prsc.ls.net.LSPacket;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.packethandler.PacketHandlerDef;
import com.prsc.ls.util.PersistenceManager;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;


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
     * Queue of LS packets
     */
    private final ConcurrentLinkedQueue<LSPacket> lsMessages = new ConcurrentLinkedQueue<LSPacket>();
    /**
     * Queue of frontend packets
     */
    private final ConcurrentLinkedQueue<FPacket> fMessages = new ConcurrentLinkedQueue<FPacket>();

    /**
     * Should we be running?
     */
    private boolean running = true;

    public LoginEngine() { // remove this
        loadPacketHandlers();
    }
    
    public Map<Integer, PacketHandler> getLoginHandlers() {
    	return LSPacketHandlers;
    }
    
    public Map<Long, PacketHandler> getUniqueHandlers() {
    	return uniqueHandlers;
    }
    
    public Map<Integer, PacketHandler> getFrontHandlers() {
    	return FPacketHandlers;
    }
    
    public void pushToMessageStack(Packet packet) {
    	if(packet instanceof LSPacket) {
    		LSPacket lsp = (LSPacket) packet;
    		lsMessages.add(lsp);
    	}
    	if(packet instanceof FPacket) {
    		FPacket fp = (FPacket) packet;
    		fMessages.add(fp);
    	}
    	processIncomingPackets();
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
    	LSPacket lpacket = null;
    	while((lpacket = lsMessages.poll()) != null) {
            PacketHandler handler = null;
            if (((handler = Server.getServer().getEngine().getUniqueHandlers().get(lpacket.getUID())) != null) 
            		|| ((handler = Server.getServer().getEngine().getLoginHandlers().get(lpacket.getID())) != null)) {
                try {
                    handler.handlePacket(lpacket, lpacket.getSession());
                    Server.getServer().getEngine().getUniqueHandlers().remove(lpacket.getUID());
                } catch (Exception e) {
                    Server.error("Exception with p[" + lpacket.getID() + "]: " + e);
				e.printStackTrace();
                }
            } else {
                Server.error("Unhandled packet from server: " + lpacket.getID());
            }
        }
    	FPacket fpacket = null;
		while((fpacket = fMessages.poll()) != null) {
            PacketHandler handler = Server.getServer().getEngine().getFrontHandlers().get(fpacket.getID());
            if (handler != null) {
                try {
                    handler.handlePacket(fpacket, fpacket.getSession());
                } catch (Exception e) {
                	e.printStackTrace();
                	Server.error("Exception with p[" + fpacket.getID() + "]: " + e);
                }
            } else {
            	
                System.out.println("Unhandled packet from frontend: " + fpacket.getID());
            }
        }
    } 
    
    @Override
    public void run() {
        while(running) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ie) {
            }
        }
    } 

    public void setHandler(long uID, PacketHandler handler) {
        uniqueHandlers.put(uID, handler);
    }
}
