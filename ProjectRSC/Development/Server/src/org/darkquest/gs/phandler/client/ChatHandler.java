package org.darkquest.gs.phandler.client;

import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.jboss.netty.channel.Channel;

public final class ChatHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player sender = (Player) session.getAttachment();
        
        if (sender.isMuted()) { // don't even do anything with the data since they're muted, less work for the game engine!
        	return;
        }
        
        sender.addMessageToChatQueue(p.getData());
    }

}