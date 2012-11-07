package org.darkquest.gs.phandler.ls;

import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


public class FriendLogout implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        long friend = p.readLong();

        switch (((LSPacket) p).getID()) {
            case 12:
                for (Player player : world.getPlayers()) {
                    if (player.isFriendsWith(friend)) {
                        player.getActionSender().sendFriendUpdate(friend, 0);
                    }
                }
                break;
            case 13:
                Player player = world.getPlayer(p.readLong());
                if (player != null) {
                    player.getActionSender().sendFriendUpdate(friend, 0);
                }
                break;
        }
    }
}