package org.darkquest.gs.builders.ls;

import org.darkquest.config.Constants;
import org.darkquest.gs.builders.LSPacketBuilder;
import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.world.World;

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
