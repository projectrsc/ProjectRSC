package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.world.World;

public final class FollowRequest implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();
        Player affectedPlayer = world.getPlayer(p.readShort());
        if (affectedPlayer == null) {
            player.setSuspiciousPlayer(true);
            return;
        }
        if (player.isBusy()) {
            player.resetPath();
            return;
        }
        if (System.currentTimeMillis() - player.getLastRun() < 3000)
            return;
        player.resetAll();
        player.setFollowing(affectedPlayer, 1);
        player.getActionSender().sendMessage("Now following " + affectedPlayer.getUsername());
        //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " started to follow " + affectedPlayer.getUsername() + " at " + player.getLocation()));
    }
}
