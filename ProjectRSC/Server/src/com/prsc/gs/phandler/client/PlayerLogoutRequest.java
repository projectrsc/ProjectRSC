package com.prsc.gs.phandler.client;

import java.net.InetSocketAddress;


import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.world.World;
import com.prsc.ls.util.DataConversions;


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
