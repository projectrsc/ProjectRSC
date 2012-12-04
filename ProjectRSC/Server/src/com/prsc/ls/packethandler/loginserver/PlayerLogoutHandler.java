package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.model.World;
import com.prsc.ls.net.Packet;
import com.prsc.ls.net.monitor.Monitor;
import com.prsc.ls.packethandler.PacketHandler;


public class PlayerLogoutHandler implements PacketHandler {


    public void handlePacket(Packet p, Channel session) throws Exception {
    	try {
        long user = p.readLong();
        p.readLong(); // what's this?
        
        /*
        if(Monitor.getInstance() != null) {
			Monitor.getInstance().onLogout(session);
		} */
        
        World world = (World) session.getAttachment();
        world.unregisterPlayer(user);
    	} catch(Exception e) {
    		System.out.println("------- EXCEPTION @ PlayerLogoutHandler: -----------");
    		e.printStackTrace();
    		
    	}
    }
}
