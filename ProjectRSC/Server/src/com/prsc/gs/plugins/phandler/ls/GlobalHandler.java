package com.prsc.gs.plugins.phandler.ls;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


public class GlobalHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER sent alert (uID: " + uID + ")");
        String message = p.readString();
        for (Player player : world.getPlayers()) {
            player.getActionSender().sendAlert(message, false);
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{5};
	}

}