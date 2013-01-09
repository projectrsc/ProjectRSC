package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.builders.ls.PrivacySettingUpdatePacketBuilder;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.world.World;


public class PrivacySettingHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private PrivacySettingUpdatePacketBuilder builder = new PrivacySettingUpdatePacketBuilder();

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();

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

}
