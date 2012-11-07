package org.darkquest.gs.phandler.ls;

import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


public class FriendLogin implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Player player = world.getPlayer(p.readLong());
        if (player == null) {
            return;
        }
        long friend = p.readLong();
        if (player.isFriendsWith(friend)) {
            player.getActionSender().sendFriendUpdate(friend, p.readShort());
        }
    }

}