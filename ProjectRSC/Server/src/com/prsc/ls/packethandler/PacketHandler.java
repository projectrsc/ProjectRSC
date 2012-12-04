package com.prsc.ls.packethandler;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.net.Packet;


public interface PacketHandler {
    public void handlePacket(Packet p, Channel session) throws Exception;
}
