package com.prsc.gs.builders.ls;

import com.prsc.config.Constants;


import com.prsc.gs.builders.LSPacketBuilder;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.model.World;

public final class StatRequestPacketBuilder {

    public static final World world = World.getWorld();

    private long uID;

    public LSPacket getPacket() {
        LSPacketBuilder packet = new LSPacketBuilder();
        packet.setUID(uID);
        packet.addInt(world.countPlayers());
        packet.addInt(world.countNpcs());
        packet.addLong(Constants.GameServer.START_TIME);
        return packet.toPacket();
    }

    public void setUID(long uID) {
        this.uID = uID;
    }
}
