package com.prsc.gs.builders.ls;

import com.prsc.gs.builders.LSPacketBuilder;

import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;


public class ReportInfoRequestPacketBuilder {
    /**
     * World instance
     */
    public static final World world = World.getWorld();
    /**
     * The player to provide information on
     */
    private Player player;
    /**
     * Packets uID
     */
    private long uID;

    public LSPacket getPacket() {
        LSPacketBuilder packet = new LSPacketBuilder();
        packet.setUID(uID);
        packet.addShort(player.getX());
        packet.addShort(player.getY());
        packet.addBytes(player.getStatus().toString().getBytes());
        return packet.toPacket();
    }

    /**
     * Sets the player to provide information on
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Sets the packet to reply to
     */
    public void setUID(long uID) {
        this.uID = uID;
    }
}
