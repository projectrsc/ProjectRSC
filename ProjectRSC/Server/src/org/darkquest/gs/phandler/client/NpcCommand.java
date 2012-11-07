package org.darkquest.gs.phandler.client;

import org.darkquest.config.Constants;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Mob;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

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
        return;
    }

}
