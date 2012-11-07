package org.darkquest.gs.phandler.ls;

import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.util.Logger;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


public class ShutdownHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER requested shutdown");
        World.getWorld().getServer().kill();
    }

}