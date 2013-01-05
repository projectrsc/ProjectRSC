package com.prsc.gs.builders.ls;

import com.prsc.gs.builders.LSPacketBuilder;

import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.util.EntityList;


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
