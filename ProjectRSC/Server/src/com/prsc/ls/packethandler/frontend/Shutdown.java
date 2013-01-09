package com.prsc.ls.packethandler.frontend;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.World;
import com.prsc.ls.net.FPacket;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packetbuilder.FPacketBuilder;
import com.prsc.ls.packethandler.PacketHandler;


public class Shutdown implements PacketHandler {
    private static final FPacketBuilder builder = new FPacketBuilder();

    public void handlePacket(Packet p, Channel session) throws Exception {
        String[] params = ((FPacket) p).getParameters();
        try {
            int worldID = Integer.parseInt(params[0]);
            if (worldID == 0) {
                for (World w : Server.getServer().getWorlds()) {
                    w.getActionSender().shutdown();
                }
            } else {
                World w = Server.getServer().getWorld(worldID);
                if (w == null) {
                    throw new Exception("Unknown world");
                }
                w.getActionSender().shutdown();
            }
            builder.setID(1);
        } catch (Exception e) {
            builder.setID(0);
        }
        FPacket packet = builder.toPacket();
        if (packet != null) {
            session.write(packet);
        }
    }

}