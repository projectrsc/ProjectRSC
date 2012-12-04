package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.World;
import com.prsc.ls.net.LSPacket;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packetbuilder.loginserver.ReplyPacketBuilder;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.util.Config;
import com.prsc.ls.util.DataConversions;

import java.sql.ResultSet;


public class BanHandler implements PacketHandler {
    private ReplyPacketBuilder builder = new ReplyPacketBuilder();

    public void handlePacket(Packet p, Channel session) throws Exception {
        final long uID = ((LSPacket) p).getUID();
        boolean banned = ((LSPacket) p).getID() == 4;
        long user = p.readLong();
        long modhash = p.readLong();

        ResultSet result = Server.db.getQuery("SELECT u.group_id, p.owner FROM `" + Config.MYSQL_TABLE_PREFIX + "users` AS u INNER JOIN `" + Config.MYSQL_TABLE_PREFIX + "players` AS p ON p.owner=u.id WHERE p.user='" + user + "'");
        //ResultSet result = Server.db.getQuery("SELECT u.group_id, p.playermod, p.owner FROM `users` AS u INNER JOIN `rsca2_players` AS p ON p.owner=u.id WHERE p.user='" + user + "'");
        
        if (!result.next()) {
            builder.setSuccess(false);
            builder.setReply("There is not an account by that username");
        } else if (banned && (result.getInt("group_id") < 3)) {
            builder.setSuccess(false);
            builder.setReply("You cannot ban a (p)mod or admin!");
        } else if (Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET `banned`='" + (banned ? "1" : "0") + "' WHERE `user` LIKE '" + user + "'") == 0) {
            builder.setSuccess(false);
            builder.setReply("There is not an account by that username");
        } else {
            World w = Server.getServer().findWorld(user);
            if (w != null) {
                w.getActionSender().logoutUser(user);
            }
            if (banned)
                Server.db.updateQuery("INSERT `" + Config.MYSQL_TABLE_PREFIX + "banlog` VALUES('" + user + "','" + modhash + "','" + (System.currentTimeMillis() / 1000) + "')");
            builder.setSuccess(true);
            builder.setReply(DataConversions.hashToUsername(user) + " has been " + (banned ? "banned" : "unbanned"));
        }
        builder.setUID(uID);

        LSPacket temp = builder.getPacket();
        if (temp != null) {
            session.write(temp);
        }

    }

}
