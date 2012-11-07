package org.darkquest.gs.phandler.client;

import java.net.InetSocketAddress;

import org.darkquest.config.Constants;
import org.darkquest.gs.builders.RSCPacketBuilder;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.util.RSA;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

public class PlayerLogin implements PacketHandler {

    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p1, Channel session) throws Exception {

        Player player = (Player) session.getAttachment();
        final String ip = ((InetSocketAddress) session.getRemoteAddress()).getAddress().toString().replaceAll("/", "");

        byte loginCode;
        try {

            byte[] data = RSA.decrypt(p1.getData());
            Packet p = new Packet(session, data);

            boolean reconnecting = (p.readByte() == 1);

            int clientVersion = p.readInt();
            
            int[] sessionKeys = new int[4];
            for (int key = 0; key < sessionKeys.length; key++) {
                sessionKeys[key] = p.readInt();
            }
            String username = "";
            String password = "";

            username = p.readString(20).trim();
            password = p.readString(20).trim();

            if (world.countPlayers() >= Constants.GameServer.MAX_PLAYERS) {
                loginCode = 10;
            } else if (clientVersion < Constants.GameServer.CLIENT_VERSION) {
                loginCode = 4;
            } else if (!player.setSessionKeys(sessionKeys)) {
                loginCode = 5;
            } else {
                player.load(username, password, 0, reconnecting);
        		//Services.lookup(DatabaseManager.class).addQuery(new LoginLog(username, ip));
                return;
            }
        } catch (Exception e) {
            System.err.println("Login exception with: " + ip);
            e.printStackTrace();
            loginCode = 4;
        }

        RSCPacketBuilder pb = new RSCPacketBuilder();
        pb.setBare(true);
        pb.addByte((byte) loginCode);
        session.write(pb.toPacket());
        player.destroy(true);
    }
}
