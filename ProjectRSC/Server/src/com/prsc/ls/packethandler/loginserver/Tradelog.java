package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.util.Config;


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
