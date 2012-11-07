package org.darkquest.gs.phandler.client;

import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;


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
