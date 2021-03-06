package com.prsc.gs.plugins.phandler.ls;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.builders.ls.PlayerListRequestPacketBuilder;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


public class PlayerListRequestHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private PlayerListRequestPacketBuilder builder = new PlayerListRequestPacketBuilder();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER requested player list (uID: " + uID + ")");
        builder.setUID(uID);
        LSPacket temp = builder.getPacket();
        if (temp != null) {
            session.write(temp);
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{2};
	}

}