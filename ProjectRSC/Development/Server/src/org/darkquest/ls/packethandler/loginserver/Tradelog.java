package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.Server;
import org.darkquest.ls.util.Config;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packethandler.PacketHandler;
import org.jboss.netty.channel.Channel;


public class Tradelog implements PacketHandler {
    public void handlePacket(Packet p, Channel session) throws Exception {
        long from = p.readLong();
        long to = p.readLong();
        int item = p.readInt();
        long amount = p.readLong();
        int x = p.readInt();
        int y = p.readInt();
        int type = p.readInt();
        long date = (System.currentTimeMillis() / 1000);
        Server.db.updateQuery("INSERT `" + Config.MYSQL_TABLE_PREFIX + "tradelog` VALUES('" + from + "','" + to + "','" + date + "','" + item + "','" + x + "','" + y + "','" + amount + "','" + type + "')");
    }
}
