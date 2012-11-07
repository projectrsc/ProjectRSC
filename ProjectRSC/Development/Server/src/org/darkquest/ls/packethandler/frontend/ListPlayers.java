package org.darkquest.ls.packethandler.frontend;

import org.darkquest.ls.Server;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.FPacket;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packetbuilder.FPacketBuilder;
import org.darkquest.ls.packethandler.PacketHandler;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;


public class ListPlayers implements PacketHandler {
    private static final FPacketBuilder builder = new FPacketBuilder();

    public void handlePacket(Packet p, final Channel session) throws Exception {
        String[] params = ((FPacket) p).getParameters();
        try {
            final int worldID = Integer.parseInt(params[0]);
            System.out.println("Frontend requested player list for world " + worldID);
            World world = Server.getServer().getWorld(worldID);
            if (world == null) {
                throw new Exception("Unknown world");
            }
            world.getActionSender().playerListRequest(new PacketHandler() {
                public void handlePacket(Packet p, Channel s) throws Exception {
                    builder.setID(1);

                    ArrayList<String> params = new ArrayList<String>();
                    int count = p.readInt();
                    for (int c = 0; c < count; c++) {
                        params.add(p.readLong() + "," + p.readShort() + "," + p.readShort() + "," + worldID);
                    }
                    builder.setParameters(params.toArray(new String[params.size()]));

                    session.write(builder.toPacket());
                }
            });
        } catch (Exception e) {
            builder.setID(0);
            session.write(builder.toPacket());
        }
    }

}