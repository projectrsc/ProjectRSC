package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Formulae;
import com.prsc.gs.builders.RSCPacketBuilder;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.util.Logger;

public final class SessionRequest implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();
        if (player.isInitialized()) {
            Logger.println("[WARNING] SessionRequest for already Initialized player!");
            return;
        }
        byte userByte = p.readByte();
        long serverKey = Formulae.generateSessionKey(userByte);

        player.setServerKey(serverKey);
        RSCPacketBuilder pb = new RSCPacketBuilder();
        pb.setBare(true);
        pb.addLong(serverKey);
        session.write(pb.toPacket());
    }
}
