package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.PlayerSave;
import com.prsc.ls.model.World;
import com.prsc.ls.net.LSPacket;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.util.Config;

import java.sql.SQLException;


public class FriendHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        World world = (World) session.getAttachment();
        Server server = Server.getServer();

        long user = p.readLong();
        long friend = p.readLong();

        World w;
        PlayerSave save = server.findSave(user, world);
        switch (((LSPacket) p).getID()) {
            case 10: // Send PM
                boolean avoidBlock = p.readByte() == 1;
                byte[] message = p.getRemainingData();
                w = server.findWorld(friend);
                if (w != null) {
                    w.getActionSender().sendPM(session, user, friend, avoidBlock, message);
                }
                break;
            case 11: // Add friend
                try {
                    save.addFriend(friend);
                    Server.db.updateQuery("INSERT INTO `" + Config.MYSQL_TABLE_PREFIX + "friends`(`user`, `friend`) VALUES('" + user + "', '" + friend + "')");
                    if (Server.db.getQuery("SELECT 1 FROM `" + Config.MYSQL_TABLE_PREFIX + "players` AS p LEFT JOIN `" + Config.MYSQL_TABLE_PREFIX + "friends` AS f ON f.user=p.user WHERE (p.block_private=0 OR f.friend='" + user + "') AND p.user='" + friend + "'").next()) {
                        w = server.findWorld(friend);
                        if (w != null) {
                            world.getActionSender().friendLogin(session, user, friend, w.getID());
                        }
                    }
                    if (Server.db.getQuery("SELECT 1 FROM `" + Config.MYSQL_TABLE_PREFIX + "players` AS p LEFT JOIN `" + Config.MYSQL_TABLE_PREFIX + "friends` AS f ON f.friend=p.user WHERE p.block_private=1 AND f.user='" + friend + "' AND p.user='" + user + "'").next()) {
                        w = server.findWorld(friend);
                        if (w != null) {
                            w.getActionSender().friendLogin(session, friend, user, world.getID());
                        }
                    }
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 12: // Remove friend
                try {
                    save.removeFriend(friend);
                    Server.db.updateQuery("DELETE FROM `" + Config.MYSQL_TABLE_PREFIX + "friends` WHERE `user` LIKE '" + user + "' AND `friend` LIKE '" + friend + "'");
                    if (Server.db.getQuery("SELECT 1 FROM `" + Config.MYSQL_TABLE_PREFIX + "players` WHERE block_private=1 AND user='" + user + "'").next()) {
                        w = server.findWorld(friend);
                        if (w != null) {
                            w.getActionSender().friendLogout(session, friend, user);
                        }
                    }
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 13: // Add ignore
                try {
                    save.addIgnore(friend);
                    Server.db.updateQuery("INSERT INTO `" + Config.MYSQL_TABLE_PREFIX + "ignores`(`user`, `ignore`) VALUES('" + user + "', '" + friend + "')");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 14: // Remove ignore
                try {
                    save.removeIgnore(friend);
                    Server.db.updateQuery("DELETE FROM `" + Config.MYSQL_TABLE_PREFIX + "ignores` WHERE `user` LIKE '" + user + "' AND `ignore` LIKE '" + friend + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
        }
    }

}
