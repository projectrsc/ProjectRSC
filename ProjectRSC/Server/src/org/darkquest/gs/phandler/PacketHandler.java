package org.darkquest.gs.phandler;

import org.darkquest.gs.connection.Packet;
import org.jboss.netty.channel.Channel;


public interface PacketHandler {
    public void handlePacket(Packet p, Channel session) throws Exception;
}
