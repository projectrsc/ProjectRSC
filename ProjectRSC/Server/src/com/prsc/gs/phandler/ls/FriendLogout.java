package com.prsc.gs.phandler.ls;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.world.World;


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