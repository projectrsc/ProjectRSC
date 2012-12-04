package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.util.Logger;
import com.prsc.gs.world.World;


public class ExceptionHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        try {
            Player player = (Player) session.getAttachment();
            Logger.error("[CLIENT] Exception from " + player.getUsername() + ": " + p.readString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}