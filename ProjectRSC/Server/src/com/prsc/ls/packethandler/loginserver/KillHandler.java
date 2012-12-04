package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.util.Config;

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
