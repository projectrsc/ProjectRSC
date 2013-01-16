package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class ChatHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        Player sender = client.getPlayer();
        
        if (sender.isMuted()) { // don't even do anything with the data since they're muted, less work for the game engine!
        	return;
        }
        
        sender.addMessageToChatQueue(p.getData());
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{145};
	}

}