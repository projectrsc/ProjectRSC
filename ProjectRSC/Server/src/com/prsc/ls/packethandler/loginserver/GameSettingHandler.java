package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.World;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.util.Config;

import java.sql.SQLException;


public class GameSettingHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        World world = (World) session.getAttachment();
        long user = p.readLong();
        boolean on = p.readByte() == 1;
        int idx = (int) p.readByte();
        switch (idx) {
            case 0: // Camera Auto
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET cameraauto=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 2: // One Mouse
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET onemouse=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 3: // Sound Off
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET soundoff=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 4: // Show Roof
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET showroof=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 5: // Auto Screenshot
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET autoscreenshot=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
            case 6: // Combat Window
                try {
                    Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET combatwindow=" + (on ? 1 : 0) + " WHERE user='" + user + "'");
                } catch (SQLException e) {
                    Server.error(e.getMessage());
                }
                break;
        }
        Server.getServer().findSave(user, world).setGameSetting(idx, on);
    }

}
