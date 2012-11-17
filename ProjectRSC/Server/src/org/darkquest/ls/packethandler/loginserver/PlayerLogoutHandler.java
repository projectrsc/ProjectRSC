package org.darkquest.ls.packethandler.loginserver;

import java.net.InetSocketAddress;


import org.darkquest.ls.model.World;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.net.filter.ConnectionFilter;
import org.darkquest.ls.packethandler.PacketHandler;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;


public class PlayerLogoutHandler implements PacketHandler {


    public void handlePacket(Packet p, Channel session) throws Exception {
    	try {
        long user = p.readLong();
        p.readLong();
		
        //System.out.println("Size: " + ConnectionFilter.getInstance(0).getCurrentClients().size());
    	final long encoded = DataConversions.IPToLong(((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress());
		//System.out.println("Encoded: " + encoded);
		int count = ConnectionFilter.getInstance(0).decrementAndGet(encoded);
		//System.out.println("Now: " + count);
        
        World world = (World) session.getAttachment();
        world.unregisterPlayer(user);
    	} catch(Exception e) {
    		System.out.println("------- EXCEPTION @ PlayerLogoutHandler: -----------");
    		e.printStackTrace();
    		
    	}
    }
}
