package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.builders.ls.GameSettingUpdatePacketBuilder;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class GameSettingHandler implements PacketHandler {
	
    public static final World world = World.getWorld();

    private GameSettingUpdatePacketBuilder builder = new GameSettingUpdatePacketBuilder();

    public void handlePacket(Packet p, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        Player player = client.getPlayer();
        
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

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{157};
	}

}
