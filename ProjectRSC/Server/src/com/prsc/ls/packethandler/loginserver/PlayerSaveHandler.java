package com.prsc.ls.packethandler.loginserver;

import org.jboss.netty.channel.Channel;

import com.prsc.ls.Server;
import com.prsc.ls.model.PlayerSave;
import com.prsc.ls.model.World;
import com.prsc.ls.net.Packet;
import com.prsc.ls.packethandler.PacketHandler;
import com.prsc.ls.util.DataConversions;


public final class PlayerSaveHandler implements PacketHandler {

    public void handlePacket(Packet p, Channel session) throws Exception {
        World world = (World) session.getAttachment();
        long usernameHash = p.readLong();
        int owner = p.readInt();
        
        PlayerSave save = Server.getServer().findSave(usernameHash, world);
        
        if (save == null) {
            System.out.println("Error loading data for: " + DataConversions.hashToUsername(usernameHash));
            return;
        } 

        if (owner != save.getOwner()) {
            System.out.println("WARNING ATTEMPTED DUPE");
        } 

        save.setOwner(owner);
        save.setLogin(p.readLong(), p.readLong());
        save.setTotals(p.readShort(), p.readShort());
        save.setLocation(p.readShort(), p.readShort());
        save.setFatigue(p.readShort());
        save.setQuestPoints(p.readShort());
        save.setMuted(p.readLong());
        save.setBotDetected(p.readByte() == 1);
        
        save.setAppearance(p.readByte(), p.readByte(), p.readByte(), p.readByte(), p.readByte(), p.readByte(), p.readByte() == 1, p.readLong());
        save.setCombatStyle(p.readByte());

        for (int i = 0; i < 18; i++) {
            save.setStat(i, p.readLong(), p.readShort());
        }

        int invCount = p.readShort();
        save.clearInvItems();
        for (int i = 0; i < invCount; i++) {
            save.addInvItem(p.readShort(), p.readInt(), p.readByte() == 1);
        }

        int bnkCount = p.readShort();
        save.clearBankItems();
        for (int i = 0; i < bnkCount; i++) {
            save.addBankItem(p.readShort(), p.readInt());
        }
        int quests = p.readInt();
        for (int i = 0; i < quests; i++) {
            int questID = p.readInt();
            int stage = p.readInt();
            save.setQuestStage(questID, stage);
        }
        int cacheSize = p.readInt();
        for (int i = 0; i < cacheSize; i++) {
            /**
             * Construct key
             */
            int keyLenght = p.readInt();
            String key = new String(p.readBytes(keyLenght));
            /**
             * Manage item type now
             * 0 - Integer
             * 1 - String
             * 2 - Boolean
             * 3 - Long
             */
            int identifier = p.readInt();
            if (identifier == 0) {
                save.getCache().put(key, p.readInt());
            }
            if (identifier == 1) {
                int stringLength = p.readInt();
                String string = new String(p.readBytes(stringLength));
                save.getCache().put(key, string);
            }
            if (identifier == 2) {
                save.getCache().put(key, (p.readInt() == 1));
            }
            if (identifier == 3) {
                save.getCache().put(key, p.readLong());
            }
        }

        save.setLastUpdate(System.currentTimeMillis());
    
        if(!world.isBlocking() && !world.getPlayerCache().contains(usernameHash)) { // push to internal cache
        	System.out.println("Saving " + save.getUsername() + "...to cache");
        	world.getPlayerCache().put(usernameHash, save);
        } else if(!save.save()) { // push to SQL instead 
        	System.out.println("Error saving: " + save.getUsername());
        }
    }

}
