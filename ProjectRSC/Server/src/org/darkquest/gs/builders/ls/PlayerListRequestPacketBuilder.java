package org.darkquest.gs.builders.ls;

import org.darkquest.gs.builders.LSPacketBuilder;
import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.util.EntityList;
import org.darkquest.gs.world.World;


public class PlayerListRequestPacketBuilder {
    /**
     * World instance
     */
    public static final World world = World.getWorld();
    /**
     * Packets uID
     */
    private long uID;

    public LSPacket getPacket() {
        EntityList<Player> players = world.getPlayers();

        LSPacketBuilder packet = new LSPacketBuilder();
        packet.setUID(uID);
        packet.addInt(players.size());
        for (Player p : players) {
            packet.addLong(p.getUsernameHash());
            packet.addShort(p.getX());
            packet.addShort(p.getY());
        }
        return packet.toPacket();
    }

    /**
     * Sets the packet to reply to
     */
    public void setUID(long uID) {
        this.uID = uID;
    }
}
