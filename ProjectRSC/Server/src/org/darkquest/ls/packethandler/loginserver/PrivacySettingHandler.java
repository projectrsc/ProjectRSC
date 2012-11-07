package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.Server;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.util.Config;
import org.darkquest.ls.packethandler.PacketHandler;
import org.jboss.netty.channel.Channel;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PrivacySettingHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        World world = (World) session.getAttachment();
        Server server = Server.getServer();

        long user = p.readLong();
        boolean on = p.readByte() == 1;
        int idx = (int) p.readByte();
        switch (idx) {
            case 0: // Chat block
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET block_chat=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 1: // Private block
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET block_private=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                    ResultSet result = Server.db.getQuery("SELECT user FROM `" + Config.MYSQL_TABLE_PREFIX + "friends` WHERE friend='" + user + "' AND user NOT IN (SELECT friend FROM `" + Config.MYSQL_TABLE_PREFIX + "friends` WHERE user='" + user + "')");
                    while (result.next()) {
                        long friend = result.getLong("user");
                        World w = server.findWorld(friend);
                        if (w != null) {
                            if (on) {
                                w.getActionSender().friendLogout(friend, user);
                            } else {
                                w.getActionSender().friendLogin(friend, user, world.getID());
                            }
                        }
                    }
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 2: // Trade block
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET block_trade=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 3: // Duel block
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET block_duel=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
        }
        server.findSave(user, world).setPrivacySetting(idx, on);
    }

}
