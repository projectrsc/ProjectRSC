package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Mob;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class NpcCommandHandler implements PacketHandler {

    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        int serverIndex = p.readShort();
        Client client = (Client) channel.getAttachment();
        final Player player = client.getPlayer();
        
        if (player.isBusy()) {
            return;
        }

        final Mob affectedMob = world.getNpc(serverIndex);
        final Npc affectedNpc = (Npc) affectedMob;
        
        if (affectedNpc == null || affectedMob == null || player == null)
            return;
        
        if (PluginHandler.getPluginHandler().blockDefaultAction("NpcCommand", new Object[]{affectedNpc, player})) {
            return;
        }
        
        return;
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{74};
	}

}
