package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.Server;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.util.Config;
import org.darkquest.ls.packethandler.PacketHandler;
import org.jboss.netty.channel.Channel;

import java.sql.SQLException;


public class KillHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        try {
            Server.db.updateQuery("INSERT INTO `" + Config.MYSQL_TABLE_PREFIX + "kills`(`user`, `killed`, `time`, `type`) VALUES('" + p.readLong() + "', '" + p.readLong() + "', " + (int) (System.currentTimeMillis() / 1000) + ", " + p.readByte() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
