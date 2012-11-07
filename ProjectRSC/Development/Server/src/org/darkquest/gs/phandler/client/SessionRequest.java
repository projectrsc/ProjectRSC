package org.darkquest.gs.phandler.client;

import org.darkquest.config.Formulae;
import org.darkquest.gs.builders.RSCPacketBuilder;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.util.Logger;
import org.jboss.netty.channel.Channel;

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
