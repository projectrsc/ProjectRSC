package com.prsc.gs.plugins.phandler.client;


import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;


public class PlayerLogoutRequestHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        Player player = client.getPlayer();

        if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerLogout", new Object[]{player}, false)) {
            player.getActionSender().sendCantLogout();
            return;
        }

        if (player.canLogout()) {
            player.destroy(true);
        } else {
            player.getActionSender().sendCantLogout();
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{129};
	}
}
