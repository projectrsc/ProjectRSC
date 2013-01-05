package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.builders.ls.PrivacySettingUpdatePacketBuilder;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;


public class PrivacySettingHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private PrivacySettingUpdatePacketBuilder builder = new PrivacySettingUpdatePacketBuilder();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        Player player = client.getPlayer();

        boolean[] newSettings = new boolean[4];
        for (int i = 0; i < 4; i++) {
            newSettings[i] = p.readByte() == 1;
        }

        builder.setPlayer(player);
        for (int i = 0; i < 4; i++) {
            builder.setIndex(i);
            if (newSettings[i] && !player.getPrivacySetting(i)) {
                builder.setOn(true);
            } else if (!newSettings[i] && player.getPrivacySetting(i)) {
                builder.setOn(false);
            } else {
                continue;
            }
            LSPacket packet = builder.getPacket();
            if (packet != null) {
                World.getWorld().getServer().getLoginConnector().getSession().write(packet);
            }
        }
        for (int i = 0; i < 4; i++) {
            player.setPrivacySetting(i, newSettings[i]);
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{176};
	}

}
