package org.darkquest.gs.phandler.ls;

import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.util.Logger;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


public class UpdateHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER sent update (uID: " + uID + ")");
        String reason = p.readString();
        if (World.getWorld().getServer().shutdownForUpdate(60)) {
            for (Player player : world.getPlayers()) {
                player.getActionSender().sendAlert("The server will be shutting down in 60 seconds: " + reason, false);
                player.getActionSender().startShutdown(60);
            }
        }
    }

}