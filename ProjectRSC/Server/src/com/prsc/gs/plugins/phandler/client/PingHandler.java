package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;


public class PingHandler implements PacketHandler {
	
    public void handlePacket(Packet p, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        Player player = client.getPlayer();
        
        if (p.getLength() > 0) {
            byte b = p.readByte();
            if (b == 1) { // 1 is for SCAR.
                if (player.sessionFlags < 1) {
                    Logger.println(player.getUsername() + " is using SCAR!");
                    player.sessionFlags++;
                }
            } 
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{5};
	}
}
