package com.prsc.gs.phandler.ls;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.builders.ls.StatRequestPacketBuilder;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.util.Logger;
import com.prsc.gs.world.World;


public class StatRequestHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private StatRequestPacketBuilder builder = new StatRequestPacketBuilder();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER requested stats (uID: " + uID + ")");
        builder.setUID(uID);
        LSPacket temp = builder.getPacket();
        if (temp != null) {
            session.write(temp);
        }
    }

}