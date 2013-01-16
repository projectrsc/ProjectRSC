package com.prsc.gs.plugins.phandler.client;

import java.net.InetSocketAddress;



import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.gs.builders.RSCPacketBuilder;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.util.RSA;

public class PlayerLoginRequestHandler implements PacketHandler {

    /**
     * World instance
     */
    public static final World world = World.getWorld();

    public void handlePacket(Packet p1, Channel channel) throws Exception {
        Client client = (Client) channel.getAttachment();
        final String ip = ((InetSocketAddress) channel.getRemoteAddress()).getAddress().toString().replaceAll("/", "");
        Player player = null;//client.getPlayer();
        byte loginCode;
        
        try {
        	player = new Player(channel);
        	player.setServerKey(client.getServerKey());
            client.setAssociatedPlayer(player);
        	
            byte[] data = RSA.decrypt(p1.getData());
            Packet p = new Packet(channel, data);

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
        channel.write(pb.toPacket());
        player.destroy(true);
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{77};
	}
}
