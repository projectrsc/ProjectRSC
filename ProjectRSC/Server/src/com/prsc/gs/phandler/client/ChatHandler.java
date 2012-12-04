package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;

public final class ChatHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player sender = (Player) session.getAttachment();
        
        if (sender.isMuted()) { // don't even do anything with the data since they're muted, less work for the game engine!
        	return;
        }
        
        sender.addMessageToChatQueue(p.getData());
    }

}