package org.darkquest.gs.phandler.client;

import java.net.InetSocketAddress;


import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.world.World;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;


public class PlayerLogoutRequest implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();

        if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerLogout", new Object[]{player}, false)) {
            player.getActionSender().sendCantLogout();
            return;
        }

        if (player.canLogout()) {
            player.destroy(true);
        } else {
            player.getActionSender().sendCantLogout();
        }
    }
}
