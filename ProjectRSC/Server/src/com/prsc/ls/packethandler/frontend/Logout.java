package com.prsc.ls.packethandler.frontend;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.World;
import com.prsc.ls.net.FPacket;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packetbuilder.FPacketBuilder;
import com.prsc.ls.packethandler.PacketHandler;


public class Logout implements PacketHandler {
    private static final FPacketBuilder builder = new FPacketBuilder();

    public void handlePacket(Packet p, Channel session) throws Exception {
        String[] params = ((FPacket) p).getParameters();
        try {
            long usernameHash = Long.parseLong(params[0]);
            World world = Server.getServer().findWorld(usernameHash);
            if (world == null) {
                throw new Exception("World not found");
            }
            world.getActionSender().logoutUser(session, usernameHash);
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