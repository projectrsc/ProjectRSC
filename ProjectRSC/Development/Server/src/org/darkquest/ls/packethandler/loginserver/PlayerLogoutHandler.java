package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.model.World;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packethandler.PacketHandler;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;


public class PlayerLogoutHandler implements PacketHandler {


    public void handlePacket(Packet p, Channel session) throws Exception {
    	try {
        long user = p.readLong();
        p.readLong();
        World world = (World) session.getAttachment();
        world.unregisterPlayer(user);
    	} catch(Exception e) {
    		System.out.println("------- EXCEPTION @ PlayerLogoutHandler: -----------");
    		e.printStackTrace();
    		
    	}
    }
}
