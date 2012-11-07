package org.darkquest.gs.phandler.client;

import org.darkquest.gs.builders.ls.GameSettingUpdatePacketBuilder;
import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

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
