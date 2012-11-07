package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.Server;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.LSPacket;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packetbuilder.loginserver.ReplyPacketBuilder;
import org.darkquest.ls.packethandler.PacketHandler;
import org.darkquest.ls.util.Config;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;

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
