package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;


public class PlayerAppearanceIDHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        int mobCount = p.readShort();
        int[] indicies = new int[mobCount];
        int[] appearanceIDs = new int[mobCount];
        for (int x = 0; x < mobCount; x++) {
            indicies[x] = p.readShort();
            appearanceIDs[x] = p.readShort();
        }
        Client client = (Client) channel.getAttachment();
        Player player = client.getPlayer();
        player.addPlayersAppearanceIDs(indicies, appearanceIDs);
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{83};
	}

}
