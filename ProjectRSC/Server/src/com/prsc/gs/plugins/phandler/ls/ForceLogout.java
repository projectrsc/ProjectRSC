package com.prsc.gs.plugins.phandler.ls;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


public class ForceLogout implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER requested player logout (uID: " + uID + ")");
        Player player = world.getPlayer(p.readLong());
        if (player != null) {
            player.getActionSender().sendLogout();
            player.destroy(true);
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{7};
	}

}