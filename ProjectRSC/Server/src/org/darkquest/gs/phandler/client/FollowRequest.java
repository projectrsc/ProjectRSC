package org.darkquest.gs.phandler.client;

import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

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
