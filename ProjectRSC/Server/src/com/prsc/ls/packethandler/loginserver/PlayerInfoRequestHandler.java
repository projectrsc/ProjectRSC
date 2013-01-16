package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.World;
import com.prsc.ls.net.LSPacket;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packetbuilder.LSPacketBuilder;
import com.prsc.ls.packethandler.PacketHandler;


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
        w.getActionSender().requestPlayerInfo(session, user, new PacketHandler() {
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