package com.prsc.gs.plugins.phandler.ls;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.builders.ls.PlayerInfoRequestPacketBuilder;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


public class PlayerInfoRequestHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private PlayerInfoRequestPacketBuilder builder = new PlayerInfoRequestPacketBuilder();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER requested player information (uID: " + uID + ")");
        builder.setUID(uID);
        builder.setPlayer(world.getPlayer(p.readLong()));
        LSPacket temp = builder.getPacket();
        if (temp != null) {
            session.write(temp);
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{9};
	}

}