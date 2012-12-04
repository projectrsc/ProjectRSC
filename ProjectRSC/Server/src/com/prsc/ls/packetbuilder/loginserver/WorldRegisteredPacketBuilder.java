package com.prsc.ls.packetbuilder.loginserver;

import com.prsc.ls.net.LSPacket;
import com.prsc.ls.packetbuilder.LSPacketBuilder;

public final class WorldRegisteredPacketBuilder {

    private boolean success;

    private long uID;

    public LSPacket getPacket() {
        LSPacketBuilder packet = new LSPacketBuilder();
        packet.setUID(uID);
        packet.addByte((byte) (success ? 1 : 0));
        return packet.toPacket();
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setUID(long uID) {
        this.uID = uID;
    }
}
