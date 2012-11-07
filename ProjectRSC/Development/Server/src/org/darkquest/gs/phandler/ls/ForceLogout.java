package org.darkquest.gs.phandler.ls;

import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.util.Logger;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


public class ForceLogout implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        long uID = ((LSPacket) p).getUID();
        Logger.event("LOGIN_SERVER requested player logout (uID: " + uID + ")");
        Player player = world.getPlayer(p.readLong());
        if (player != null) {
            player.getActionSender().sendLogout();
            player.destroy(true);
        }
    }

}