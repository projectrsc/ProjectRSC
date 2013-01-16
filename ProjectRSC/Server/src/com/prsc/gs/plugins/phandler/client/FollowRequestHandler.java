package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.core.GameEngine;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class FollowRequestHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel channel) throws Exception {
    	Client client = (Client) channel.getAttachment();
        Player player = client.getPlayer();
        Player affectedPlayer = world.getPlayer(p.readShort());
        
        if (affectedPlayer == null) {
            player.setSuspiciousPlayer(true);
            return;
        }
        if (player.isBusy()) {
            player.resetPath();
            return;
        }
        if (GameEngine.getAccurateTimestamp() - player.getLastRun() < 3000)
            return;
        player.resetAll();
        player.setFollowing(affectedPlayer, 1);
        player.getActionSender().sendMessage("Now following " + affectedPlayer.getUsername());
        //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " started to follow " + affectedPlayer.getUsername() + " at " + player.getLocation()));
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{68};
	}
}
