package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Formulae;
import com.prsc.gs.builders.RSCPacketBuilder;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.Logger;

public final class SessionRequestHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        byte userByte = p.readByte();
        long sessionKey = Formulae.generateSessionKey(userByte);
        Player player = null;
        
        try {
        	player = client.getPlayer();
        	
        	if (player != null && player.isInitialized()) {
                Logger.println("[WARNING] Request for already initialized player");
                return;
            } 
        } catch(Exception e) {
        	// ignore as this will happen
        } finally {
        	client.setServerKey(sessionKey); // FIX
            channel.write(new RSCPacketBuilder().setBare(true).addLong(sessionKey).toPacket());
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{32};
	}
}
