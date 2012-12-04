package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.world.World;

public final class InvUseOnItem implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();
        if (player.isBusy()) {
            player.resetPath();
            return;
        }
        player.resetAll();
        InvItem item1 = player.getInventory().get(p.readShort());
        InvItem item2 = player.getInventory().get(p.readShort());
        if (item1 == null || item2 == null) {
            player.setSuspiciousPlayer(true);
            return;
        }

        if (item1.getDef().isMembers() || item2.getDef().isMembers()) {
            if (!Constants.GameServer.MEMBER_WORLD) {
                player.sendMemberErrorMessage();
                return;
            }
        }

        //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used item " + item1 + " on item " + item2 + " at " + player.getLocation()));

        if (PluginHandler.getPluginHandler().blockDefaultAction("InvUseOnItem", new Object[]{player, item1, item2})) {
            return;
        }

       // player.getActionSender().sendMessage("Nothing interesting happens");
    }
}
