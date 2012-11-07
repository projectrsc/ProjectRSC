package org.darkquest.ls.packethandler.frontend;

import org.darkquest.ls.Server;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.FPacket;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packetbuilder.FPacketBuilder;
import org.darkquest.ls.packethandler.PacketHandler;
import org.jboss.netty.channel.Channel;


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
            world.getActionSender().logoutUser(usernameHash);
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