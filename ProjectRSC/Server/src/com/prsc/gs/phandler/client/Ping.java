package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


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
