package com.prsc.gs.plugins.phandler.ls;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


public class UpdateHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER sent update (uID: " + uID + ")");
        String reason = p.readString();
        if (World.getWorld().getServer().shutdownForUpdate(60)) {
            for (Player player : world.getPlayers()) {
                player.getActionSender().sendAlert("The server will be shutting down in 60 seconds: " + reason, false);
                player.getActionSender().startShutdown(60);
            }
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{4};
	}

}