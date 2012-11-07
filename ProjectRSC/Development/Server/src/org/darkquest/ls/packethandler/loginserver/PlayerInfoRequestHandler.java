package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.Server;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.LSPacket;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packetbuilder.LSPacketBuilder;
import org.darkquest.ls.packethandler.PacketHandler;
import org.jboss.netty.channel.Channel;


public class PlayerInfoRequestHandler implements PacketHandler {

    public void handlePacket(Packet p, final Channel session) throws Exception {
        final long uID = ((LSPacket) p).getUID();
        final long user = p.readLong();
        final World w = Server.getServer().findWorld(user);
        if (w == null) {
            LSPacketBuilder builder = new LSPacketBuilder();
            builder.setUID(uID);
            builder.addByte((byte) 0);
            session.write(builder.toPacket());
            return;
        }
        w.getActionSender().requestPlayerInfo(user, new PacketHandler() {
            public void handlePacket(Packet p, Channel s) throws Exception {
                LSPacketBuilder builder = new LSPacketBuilder();
                builder.setUID(uID);
                if (p.readByte() == 0) {
                    builder.addByte((byte) 0);
                } else {
                    builder.addByte((byte) 1);
                    builder.addShort(w == null ? 0 : w.getID());
                    builder.addBytes(p.getRemainingData());
                }
                session.write(builder.toPacket());
            }
        });

    }

}