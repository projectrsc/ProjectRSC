package org.darkquest.ls.packethandler;

import org.darkquest.ls.net.Packet;
import org.jboss.netty.channel.Channel;


public interface PacketHandler {
    public void handlePacket(Packet p, Channel session) throws Exception;
}
