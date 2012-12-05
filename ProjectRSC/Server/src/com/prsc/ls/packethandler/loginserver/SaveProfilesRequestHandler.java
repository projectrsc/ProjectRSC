package com.prsc.ls.packethandler.loginserver;


import org.jboss.netty.channel.Channel;

import com.prsc.ls.model.PlayerSave;
import com.prsc.ls.model.World;
import com.prsc.ls.net.LSPacket;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packetbuilder.loginserver.ReplyPacketBuilder;
import com.prsc.ls.packethandler.PacketHandler;

import java.util.Iterator;
import java.util.Map.Entry;

public final class SaveProfilesRequestHandler implements PacketHandler {

    private final ReplyPacketBuilder builder = new ReplyPacketBuilder();

    public void handlePacket(Packet p, final Channel session) throws Exception {
        final long uID = ((LSPacket) p).getUID();
        World world = (World) session.getAttachment();
        boolean shutdownEvent = p.readByte() == 1 ? true : false;
        System.out.println("World " + world.getID() + " requested to save all profiles");

        Iterator<Entry<Long, PlayerSave>> iterator = world.getAssosiatedSaves().iterator();

        while (iterator.hasNext()) { // current players online
            PlayerSave profile = iterator.next().getValue();
            profile.save();
        	iterator.remove(); 
        }
        /*
        for(Entry<Long, PlayerSave> saves : world.getPlayerCache().entrySet()) {
        	System.out.println("Found cached entry, saving and removing...");
            PlayerSave profile = saves.getValue();
            world.getPlayerCache().get(profile.getUser()).save();
        }
        world.getPlayerCache().clear();
        
        if(shutdownEvent) {
        	System.out.println("Blocking cached access");
        	world.setBlocking(true);
        } */

        builder.setUID(uID);
        builder.setSuccess(true);

        LSPacket packet = builder.getPacket();
        if (packet != null) {
            session.write(packet);
        }
    }

}
