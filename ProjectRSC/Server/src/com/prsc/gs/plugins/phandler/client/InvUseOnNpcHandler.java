package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.event.impl.WalkToMobEvent;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.states.Action;


public class InvUseOnNpcHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        Player player = client.getPlayer();
        
        if (player.isBusy()) {
            player.resetPath();
            return;
        }
        player.resetAll();
        int sh = -125534; // what the fuck?
        try {
            sh = p.readShort();
        } catch (NullPointerException npe) {
            return;
        }

        final Npc affectedNpc = world.getNpc(sh);
        final InvItem item = player.getInventory().get(p.readShort());
        if (affectedNpc == null || item == null) { // This shouldn't happen
            return;
        }

        //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used item " + item + " on npc " + affectedNpc.getDef().getName() + " at " + player.getLocation()));

        player.setFollowing(affectedNpc);
        player.setStatus(Action.USING_INVITEM_ON_NPC);
        World.getWorld().getDelayedEventHandler().add(new WalkToMobEvent(player, affectedNpc, 1) {
            public void arrived() {
                owner.resetPath();
                owner.resetFollowing(); //|| !owner.nextTo(affectedNpc)
                if (!owner.getInventory().contains(item) || owner.isBusy() || owner.isRanging() || !owner.withinRange(affectedNpc, 1) || affectedNpc.isBusy() || owner.getStatus() != Action.USING_INVITEM_ON_NPC) {
                    return;
                }
                owner.resetAll();

                if (item.getDef().isMembers() && !Constants.GameServer.MEMBER_WORLD) {
                    owner.getActionSender().sendMessage("Nothing interesting happens.");
                    return;
                }
                
                if(PluginHandler.getPluginHandler().blockDefaultAction("InvUseOnNpc", new Object[]{owner, affectedNpc, item})) {
                	return;
                }
            }
        });
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{142};
	}

}
