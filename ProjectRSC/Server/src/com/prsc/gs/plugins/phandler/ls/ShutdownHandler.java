package com.prsc.gs.plugins.phandler.ls;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


public class ShutdownHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER requested shutdown");
        World.getWorld().getServer().kill();
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{3};
	}

}