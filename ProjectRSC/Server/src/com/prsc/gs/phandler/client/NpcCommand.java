package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Mob;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.world.World;

public final class NpcCommand implements PacketHandler {

    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        int serverIndex = p.readShort();
        final Player player = (Player) session.getAttachment();
        if (player.isBusy()) {
            return;
        }

        final Mob affectedMob = world.getNpc(serverIndex);
        final Npc affectedNpc = (Npc) affectedMob;
        
        if (affectedNpc == null || affectedMob == null || player == null)
            return;
        
        if (Constants.GameServer.MEMBER_WORLD) {
        	player.sendMemberErrorMessage();
        }
        
        if (PluginHandler.getPluginHandler().blockDefaultAction("NpcCommand", new Object[]{affectedNpc, player})) {
            return;
        }
        return;
    }

}
