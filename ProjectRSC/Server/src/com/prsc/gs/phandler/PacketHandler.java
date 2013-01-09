package com.prsc.gs.phandler;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;


public interface PacketHandler {
	
	//public int[] bindedIds();
    public void handlePacket(Packet p, Channel session) throws Exception;
}
