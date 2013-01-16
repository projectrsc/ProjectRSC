package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


public class ExceptionHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        try {
            Client client = (Client) channel.getAttachment();
            Player player = client.getPlayer();
            Logger.error("[CLIENT] Exception from " + player.getUsername() + ": " + p.readString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{156};
	}
}