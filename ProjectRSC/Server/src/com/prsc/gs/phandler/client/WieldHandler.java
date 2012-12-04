package com.prsc.gs.phandler.client;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.config.Formulae;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.world.World;


public final class WieldHandler implements PacketHandler {

    public static final World world = World.getWorld();

    public void handlePacket(Packet p, Channel session) throws Exception {
        Player player = (Player) session.getAttachment();
        int pID = ((RSCPacket) p).getID();

        if (player.isBusy() && !player.inCombat()) {
            return;
        }

        if (player.isDueling() && player.getDuelSetting(3)) {
            player.getActionSender().sendMessage("Armour is disabled in this duel");
            return;
        }

        player.resetAllExceptDueling();
        int idx = (int) p.readShort();

        if (idx < 0 || idx >= 30) {
            player.setSuspiciousPlayer(true);
            return;
        }

        InvItem item = player.getInventory().get(idx);
        
        if (item == null || !item.isWieldable()) {
            player.setSuspiciousPlayer(true);
            return;
        }

        if (!Constants.GameServer.MEMBER_WORLD && item.getDef().isMembers()) {
            player.getActionSender().sendMessage("You need to be a member to use this object");
            return;
        }

        switch (pID) {
            case 181:
                if (!item.isWielded()) {
                    if (PluginHandler.getPluginHandler().blockDefaultAction("Wield", new Object[]{player, item}))
                        return;
                    wieldItem(player, item);
                }
                break;
            case 92:
                if (item.isWielded()) {
                    if (PluginHandler.getPluginHandler().blockDefaultAction("UnWield", new Object[]{player, item}))
                        return;
                    unWieldItem(player, item, true);
                }
                break;
        }

        player.getActionSender().sendInventory();
        player.getActionSender().sendEquipmentStats();
    }

    public static void unWieldItem(Player player, InvItem item, boolean sound) {
    	if(item == null) {
    		System.out.println("Null Item!");
    		return;
    		
    	} else if(player == null) {
    		System.out.println("Null Player!");
    		return;
    	} else if(item.getWieldableDef() == null) {
    		System.out.println(player.getUsername() + " tried to wield an unwieldable item?");
    		return;
    	} else if(player.getPlayerAppearance() == null) {
    		System.out.println("Null Player APpearance!");
    		return;
    	}
        item.setWield(false);
        if (sound) {
            player.getActionSender().sendSound("click");
        }
        player.updateWornItems(item.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(item.getWieldableDef().getWieldPos()));
    }

    private void wieldItem(Player player, InvItem item) {
        String youNeed = "";
        for (Entry<Integer, Integer> e : item.getWieldableDef().getStatsRequired()) {
            if (player.getMaxStat(e.getKey()) < e.getValue()) {
                youNeed += e.getValue() + " " + Formulae.statArray[e.getKey()] + ", ";
            }
        }
        if (!youNeed.equals("")) {
            player.getActionSender().sendMessage("You must have at least " + youNeed.substring(0, youNeed.length() - 2) + " to use this item.");
            return;
        }
        if (EntityHandler.getItemWieldableDef(item.getID()).femaleOnly() && player.isMale()) {
            player.getActionSender().sendMessage("This piece of armor is for a female only.");
            return;
        }
        if (item.getID() == 407 || item.getID() == 401) {
            if (player.getCurStat(6) < 31) {
                player.getActionSender().sendMessage("You must have at least 31 magic");
                return;
            }
        }
        ArrayList<InvItem> items = player.getInventory().getItems();
        for (InvItem i : items) {
            if (item.wieldingAffectsItem(i) && i.isWielded()) {
                unWieldItem(player, i, false);
            }
        }

        item.setWield(true);
        player.getActionSender().sendSound("click");
        player.updateWornItems(item.getWieldableDef().getWieldPos(), item.getWieldableDef().getSprite());
    }

}