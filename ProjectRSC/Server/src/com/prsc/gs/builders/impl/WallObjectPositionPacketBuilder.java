package com.prsc.gs.builders.impl;

import com.prsc.gs.builders.RSCPacketBuilder;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.Player;
import com.prsc.gs.tools.DataConversions;
import com.prsc.gs.util.StatefulEntityCollection;

import java.util.Collection;


public class WallObjectPositionPacketBuilder {
	
    private Player player;

    public RSCPacket getPacket() {
        StatefulEntityCollection<GameObject> watchedObjects = player.getWatchedObjects();
        if (watchedObjects.changed()) {
            Collection<GameObject> newObjects = watchedObjects.getNewEntities();
            Collection<GameObject> knownObjets = watchedObjects.getKnownEntities();
            RSCPacketBuilder packet = new RSCPacketBuilder();
            packet.setID(95);
            for (GameObject o : knownObjets) {
                if (o.getType() != 1) {
                    continue;
                }
                // We should remove ones miles away differently I think
                if (watchedObjects.isRemoving(o)) {
                    byte[] offsets = DataConversions.getObjectPositionOffsets(o.getLocation(), player.getLocation());
                    packet.addShort(60000);
                    packet.addByte(offsets[0]);
                    packet.addByte(offsets[1]);
                    packet.addByte((byte) o.getDirection());
                }
            }
            for (GameObject o : newObjects) {
                if (o.getType() != 1) {
                    continue;
                }
                byte[] offsets = DataConversions.getObjectPositionOffsets(o.getLocation(), player.getLocation());
                packet.addShort(o.getID());
                packet.addByte(offsets[0]);
                packet.addByte(offsets[1]);
                packet.addByte((byte) o.getDirection());
            }
            return packet.toPacket();
        }
        return null;
    }
    
    public void setPlayer(Player player) {
    	this.player = player;
    }
}
