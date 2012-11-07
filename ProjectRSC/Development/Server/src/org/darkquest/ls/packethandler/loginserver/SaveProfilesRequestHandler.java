package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.model.PlayerSave;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.LSPacket;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packetbuilder.loginserver.ReplyPacketBuilder;
import org.darkquest.ls.packethandler.PacketHandler;
import org.jboss.netty.channel.Channel;

import java.util.Iterator;
import java.util.Map.Entry;

public final class SaveProfilesRequestHandler implements PacketHandler {

    private final ReplyPacketBuilder builder = new ReplyPacketBuilder();

    public void handlePacket(Packet p, final Channel session) throws Exception {
        final long uID = ((LSPacket) p).getUID();
        World world = (World) session.getAttachment();
        System.out.println("World " + world.getID() + " requested to save all profiles");

        Iterator<Entry<Long, PlayerSave>> iterator = world.getAssosiatedSaves().iterator();

        while (iterator.hasNext()) {
            PlayerSave profile = iterator.next().getValue();
            profile.save();
            iterator.remove();
        }

        builder.setUID(uID);
        builder.setSuccess(true);

        LSPacket packet = builder.getPacket();
        if (packet != null) {
            session.write(packet);
        }
    }

}
