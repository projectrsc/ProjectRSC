package org.darkquest.gs.phandler.client;

import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.util.Logger;
import org.jboss.netty.channel.Channel;


public class Ping implements PacketHandler {
    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();
        if (p.getLength() > 0) {
            byte b = p.readByte();
            if (b == 1) { // 1 is for SCAR.
                if (player.sessionFlags < 1) {
                    Logger.println(player.getUsername() + " is using SCAR!");
                    player.sessionFlags++;
                }
            }
        }
    }
}
