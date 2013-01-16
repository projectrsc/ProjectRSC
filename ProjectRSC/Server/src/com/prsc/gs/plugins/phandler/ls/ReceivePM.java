package com.prsc.gs.plugins.phandler.ls;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;


public class ReceivePM implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        long sender = p.readLong();
        Player recipient = world.getPlayer(p.readLong());
        boolean avoidBlock = p.readByte() == 1;
        if (recipient == null || !recipient.loggedIn()) {
            return;
        }
        if (recipient.getPrivacySetting(1) && !recipient.isFriendsWith(sender) && !avoidBlock) {
            return;
        }
        if (recipient.isIgnoring(sender) && !avoidBlock) {
            return;
        }
        recipient.getActionSender().sendPrivateMessage(sender, p.getRemainingData());
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{10};
	}

}