package com.prsc.gs.plugins.phandler;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;


public interface PacketHandler {
	
	public int[] getAssociatedIdentifiers();
    public void handlePacket(Packet p, Channel session) throws Exception;
}
