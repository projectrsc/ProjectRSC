package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.builders.ls.GameSettingUpdatePacketBuilder;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.world.World;

public final class GameSettingHandler implements PacketHandler {
	
    public static final World world = World.getWorld();

    private GameSettingUpdatePacketBuilder builder = new GameSettingUpdatePacketBuilder();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        Player player = (Player) channel.getAttachment();
        int idx = (int) p.readByte();
        if (idx < 0 || idx > 6) {
            player.setSuspiciousPlayer(true);
            return;
        }
        boolean on = p.readByte() == 1;
        player.setGameSetting(idx, on);

        builder.setPlayer(player);
        builder.setIndex(idx);
        builder.setOn(on);

        LSPacket packet = builder.getPacket();
        if (packet != null) {
            World.getWorld().getServer().getLoginConnector().getSession().write(packet);
        }
    }

}
