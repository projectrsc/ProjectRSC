package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.World;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.util.Config;

import java.sql.SQLException;

public final class ReportHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        World world = (World) session.getAttachment();

        final long user = p.readLong();
        final long reported = p.readLong();
        final byte reason = p.readByte();

        world.getActionSender().requestReportInfo(reported, new PacketHandler() {
            public void handlePacket(Packet p, Channel session) throws Exception {
                int x = p.readShort();
                int y = p.readShort();
                String status = p.readString();
                try {
                    Server.db.updateQuery("INSERT INTO `" + Config.MYSQL_TABLE_PREFIX + "reports`(`from`, `about`, `time`, `reason`, `x`, `y`, `status`) VALUES('" + user + "', '" + reported + "', '" + (System.currentTimeMillis() / 1000) + "', '" + reason + "', '" + x + "', '" + y + "', '" + status + "')");
                } catch (SQLException e) {
                    Server.error(e);
                }
            }
        });
    }

}
