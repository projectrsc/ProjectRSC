package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.world.World;


public class PlayerAppearanceIDHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        int mobCount = p.readShort();
        int[] indicies = new int[mobCount];
        int[] appearanceIDs = new int[mobCount];
        for (int x = 0; x < mobCount; x++) {
            indicies[x] = p.readShort();
            appearanceIDs[x] = p.readShort();
        }
        Player player = (Player) session.getAttachment();
        player.addPlayersAppearanceIDs(indicies, appearanceIDs);
    }

}
