package com.prsc.ls.packetbuilder.loginserver;

import com.prsc.ls.Server;

import com.prsc.ls.core.LoginEngine;
import com.prsc.ls.packetbuilder.LSPacketBuilder;
import com.prsc.ls.packethandler.PacketHandler;
import org.jboss.netty.channel.Channel;


public class MiscPacketBuilder {
    /**
     * LoginEngine
     */
    private LoginEngine engine = Server.getServer().getEngine();
    
    /**
     * List of packets waiting to be sent to the world
     */

    public void alert(Channel channel, long user, String message) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(6);
        s.addLong(user);
        s.addBytes(message.getBytes());
        channel.write(s.toPacket());
    }

    public void alert(Channel channel, String message) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(5);
        s.addBytes(message.getBytes());
        channel.write(s.toPacket());
    }

    public void friendLogin(Channel channel, long user, long friend, int w) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(11);
        s.addLong(user);
        s.addLong(friend);
        s.addShort(w);
        channel.write(s.toPacket());
    }

    public void friendLogout(Channel channel, long friend) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(12);
        s.addLong(friend);
        channel.write(s.toPacket());
    }

    public void friendLogout(Channel channel, long user, long friend) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(13);
        s.addLong(friend);
        s.addLong(user);
        channel.write(s.toPacket());
    }

    public void logoutUser(Channel channel, long user) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(7);
        s.addLong(user);
        channel.write(s.toPacket());
    }

    public void playerListRequest(Channel channel, PacketHandler handler) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(2);
        s.setHandler(engine, handler);
        channel.write(s.toPacket());
    }

    public void requestPlayerInfo(Channel channel, long user, PacketHandler handler) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(9);
        s.addLong(user);
        s.setHandler(engine, handler);
        channel.write(s.toPacket());
    }

    public void requestReportInfo(Channel channel, long user, PacketHandler handler) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(8);
        s.addLong(user);
        s.setHandler(engine, handler);
        channel.write(s.toPacket());
    }

    /* NOT IN USE
    public void requestStats(PacketHandler handler) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(1);
        s.setHandler(engine, handler);
        packets.add(s.toPacket());
    } */

    public void sendPM(Channel channel, long user, long friend, boolean avoidBlock, byte[] message) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(10);
        s.addLong(user);
        s.addLong(friend);
        s.addByte((byte) (avoidBlock ? 1 : 0));
        s.addBytes(message);
        channel.write(s.toPacket());
    }

    public void shutdown(Channel channel) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(3);
        channel.write(s.toPacket());
    }

    public void update(Channel channel, String reason) {
        LSPacketBuilder s = new LSPacketBuilder();
        s.setID(4);
        s.addBytes(reason.getBytes());
        channel.write(s.toPacket());
    }

}