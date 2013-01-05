package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;


public class StyleHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        Player player = client.getPlayer();
        int style = p.readByte();
        if (style < 0 || style > 3) {
            player.setSuspiciousPlayer(true);
            return;
        }
        player.setCombatStyle(style);
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{42};
	}

}