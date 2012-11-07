package org.darkquest.gs.phandler.ls;

import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


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

}