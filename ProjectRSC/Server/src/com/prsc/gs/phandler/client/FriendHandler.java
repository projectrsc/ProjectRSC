package com.prsc.gs.phandler.client;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants.GameServer;
import com.prsc.gs.builders.ls.MiscPacketBuilder;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.tools.DataConversions;
import com.prsc.gs.world.World;

public final class FriendHandler implements PacketHandler {

	private final World world = World.getWorld();

    private final MiscPacketBuilder loginSender = World.getWorld().getServer().getLoginConnector().getActionSender();


    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();
        int pID = ((RSCPacket) p).getID();
        
        long user = player.getUsernameHash();
        long friend = p.readLong();
        switch (pID) {
            case 168: // Add friend
            	System.out.println("Friend count: " + player.friendCount());
                if (player.friendCount() >= 100) {
                    player.getActionSender().sendMessage("Your friends list is full");
                    player.getActionSender().sendFriendList();
                    return;
                }
                // The following block of code stops duplicate entries of friends in the database
                // TODO: Implement non-existant player checks
				loginSender.addFriend(user, friend);
                player.addFriend(friend, 0);
                //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " added friend " + DataConversions.hashToUsername(friend) + " at " + player.getLocation()));
                break;
            case 52: // Remove friend
                loginSender.removeFriend(user, friend);
                player.removeFriend(friend);
                //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " removed friend " + DataConversions.hashToUsername(friend) + " at " + player.getLocation()));
                break;
            case 25: // Add ignore
                if (player.ignoreCount() >= 100) {
                    player.getActionSender().sendMessage("Your ignore list is full");
                    return;
                }
                loginSender.addIgnore(user, friend);
                player.addIgnore(friend);
                /*
                else if(!playerExists(player, friend)) {
                	if(canAddNewPlayer(friend)) {
                		loginSender.addIgnore(user, friend);
                        player.addIgnore(friend);
                	} else {
                		player.getActionSender().sendMessage("Unable to add ignore - unknown player");
                	}
                } */
                //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " added ignore " + DataConversions.hashToUsername(friend) + " at " + player.getLocation()));
                break;
            case 108: // Remove ignore
                loginSender.removeIgnore(user, friend);
                player.removeIgnore(friend);
                //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " removed friend " + DataConversions.hashToUsername(friend) + " at " + player.getLocation()));
                break;
            case 254: // Send PM
                try {
                    byte[] data = p.getRemainingData();
                    String s = DataConversions.byteToString(data, 0, data.length);
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(DataConversions.hashToUsername(friend));
                    loginSender.sendPM(user, friend, player.isAdmin(), data);
                    //Services.lookup(DatabaseManager.class).addQuery(new PMLog(player.getUsername(), s, DataConversions.hashToUsername(friend)));
                } catch (NegativeArraySizeException e) {
                    player.destroy(false);
                }
                break;
        }
    }
    
    private boolean playerExists(Player player, long friend) {
    	Iterator<Entry<Long, Integer>> friends = player.getFriendList().iterator();
        while(friends.hasNext()) {
        	if(friends.next().getKey() == friend) {
        		return true;
            }
        }
        return false;
    }
    
    private boolean canAddNewPlayer(long friend) throws SQLException {
    	if(World.getWorld().getDB().getConnection().createStatement().executeQuery("SELECT * FROM `" + GameServer.MYSQL_TABLE_PREFIX + "players` WHERE user='" + friend + "'").next()) {
    		return true;
    	}
    	return false;
    }
}
