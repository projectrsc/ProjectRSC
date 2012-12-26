package com.prsc.gs.phandler.client;

import java.util.ArrayList;


import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.db.DatabaseManager;
import com.prsc.gs.db.query.TradeLog;
import com.prsc.gs.external.ItemDef;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Inventory;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.service.Services;
import com.prsc.gs.util.Logger;
import com.prsc.gs.world.World;


public class TradeHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private boolean busy(Player player) {
        return player.isBusy() || player.isRanging() || player.accessingBank() || player.isDueling();
    }

    public void handlePacket(Packet p, Channel session) throws Exception {
    	try {
            Player player = (Player) session.getAttachment();
            int pID = ((RSCPacket) p).getID();
            Player affectedPlayer = null;

            if (busy(player)) {
                affectedPlayer = player.getWishToTrade();
                unsetOptions(player);
                unsetOptions(affectedPlayer);
                return;
            }
            
            switch (pID) {
                case 166: // Sending trade request
                	if(world != null) {
                		affectedPlayer = world.getPlayer(p.readShort());
                	}
                    
                    if (affectedPlayer == null) {
                        return;
                    }
                    
                    if (affectedPlayer.isTrading()) {
                        player.getActionSender().sendMessage("That person is already trading");
                        return;
                    }
                    if (affectedPlayer == null || affectedPlayer.isDueling() || !player.withinRange(affectedPlayer, 8) || player.isTrading() || player.tradeDuelThrottling()) {
                        unsetOptions(player);
                        return;
                    }
                    if (affectedPlayer == player) {
                        player.setSuspiciousPlayer(true);
                        Logger.println("Warning: " + player.getUsername() + " tried to trade to himself.");
                        unsetOptions(player);
                        return;
                    }
                    
                    
                    if ((affectedPlayer.getPrivacySetting(2) && !affectedPlayer.isFriendsWith(player.getUsernameHash())) || affectedPlayer.isIgnoring(player.getUsernameHash())) {
                        player.getActionSender().sendMessage("This player has trade requests blocked.");
                        return;
                    }
                    
                    if(!affectedPlayer.withinRange(player.getLocation(), 3)) { // if the person we're trading is not within 3 sqs
                    	return;
                    }

                    player.setWishToTrade(affectedPlayer);
                    player.getActionSender().sendMessage(affectedPlayer.isTrading() ? affectedPlayer.getUsername() + " is already in a trade" : "Sending trade request");
                    affectedPlayer.getActionSender().sendMessage(player.getUsername() + " wishes to trade with you");

                    if (!player.isTrading() && affectedPlayer.getWishToTrade() != null && affectedPlayer.getWishToTrade().equals(player) && !affectedPlayer.isTrading()) {
                        player.setTrading(true);
                        player.resetPath();
                        player.resetAllExceptTrading();
                        affectedPlayer.setTrading(true);
                        affectedPlayer.resetPath();
                        affectedPlayer.resetAllExceptTrading();

                        player.getActionSender().sendTradeWindowOpen();
                        affectedPlayer.getActionSender().sendTradeWindowOpen();
                        //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " sent trade request " + affectedPlayer.getUsername() + " at " + player.getLocation()));
                    } 
                    break;
                case 211: // Trade accepted
                    affectedPlayer = player.getWishToTrade();
                    if (affectedPlayer == null || busy(affectedPlayer) || !player.isTrading() || !affectedPlayer.isTrading()) { // This
                        player.setSuspiciousPlayer(true);
                        unsetOptions(player);
                        unsetOptions(affectedPlayer);
                        return;
                    }

                    player.setTradeOfferAccepted(true);

                    player.getActionSender().sendTradeAcceptUpdate();
                    affectedPlayer.getActionSender().sendTradeAcceptUpdate();

                    if (affectedPlayer.isTradeOfferAccepted()) {
                        player.getActionSender().sendTradeAccept();
                        affectedPlayer.getActionSender().sendTradeAccept();
                    }
                    //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " accepted trade request with " + affectedPlayer.getUsername() + " at " + player.getLocation()));
                    break;
                case 53: // Confirm accepted
                    affectedPlayer = player.getWishToTrade();
                    if (affectedPlayer == null || busy(affectedPlayer) || !player.isTrading() || !affectedPlayer.isTrading() || !player.isTradeOfferAccepted() || !affectedPlayer.isTradeOfferAccepted()) { // This
                        // shouldn't
                        // happen
                        player.setSuspiciousPlayer(true);
                        unsetOptions(player);
                        unsetOptions(affectedPlayer);
                        return;
                    }
                    player.setTradeConfirmAccepted(true);

                    if (affectedPlayer.isTradeConfirmAccepted()) {
                        ArrayList<InvItem> myOffer = player.getTradeOffer();
                        ArrayList<InvItem> theirOffer = affectedPlayer.getTradeOffer();

                        int myRequiredSlots = player.getInventory().getRequiredSlots(theirOffer);
                        int myAvailableSlots = (30 - player.getInventory().size()) + player.getInventory().getFreedSlots(myOffer);

                        int theirRequiredSlots = affectedPlayer.getInventory().getRequiredSlots(myOffer);
                        int theirAvailableSlots = (30 - affectedPlayer.getInventory().size()) + affectedPlayer.getInventory().getFreedSlots(theirOffer);

                        if (theirRequiredSlots > theirAvailableSlots) {
                            player.getActionSender().sendMessage("The other player does not have room to accept your items.");
                            affectedPlayer.getActionSender().sendMessage("You do not have room in your inventory to hold those items.");
                            unsetOptions(player);
                            unsetOptions(affectedPlayer);
                            return;
                        }
                        if (myRequiredSlots > myAvailableSlots) {
                            player.getActionSender().sendMessage("You do not have room in your inventory to hold those items.");
                            affectedPlayer.getActionSender().sendMessage("The other player does not have room to accept your items.");
                            unsetOptions(player);
                            unsetOptions(affectedPlayer);
                            return;
                        }

                        for (InvItem item : myOffer) {
                            InvItem affectedItem = player.getInventory().get(item);
                            if (affectedItem == null) {
                                player.setSuspiciousPlayer(true);
                                unsetOptions(player);
                                unsetOptions(affectedPlayer);
                                return;
                            }
                            if (affectedItem.isWielded()) {
                                affectedItem.setWield(false);
                                player.updateWornItems(affectedItem.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(affectedItem.getWieldableDef().getWieldPos()));
                            }
                            player.getInventory().remove(item);
                        }
                        for (InvItem item : theirOffer) {
                            InvItem affectedItem = affectedPlayer.getInventory().get(item);
                            if (affectedItem == null) {
                                affectedPlayer.setSuspiciousPlayer(true);
                                unsetOptions(player);
                                unsetOptions(affectedPlayer);
                                return;
                            }
                            if (affectedItem.isWielded()) {
                                affectedItem.setWield(false);
                                affectedPlayer.updateWornItems(affectedItem.getWieldableDef().getWieldPos(), affectedPlayer.getPlayerAppearance().getSprite(affectedItem.getWieldableDef().getWieldPos()));
                            }
                            affectedPlayer.getInventory().remove(item);
                        }
                        
                        for (InvItem item : myOffer) {
                            affectedPlayer.getInventory().add(item);
                        }
                        for (InvItem item : theirOffer) {
                            player.getInventory().add(item);
                        }

                        Services.lookup(DatabaseManager.class).addQuery(new TradeLog(player.getUsername(), affectedPlayer.getUsername(), myOffer, theirOffer).build());

                        player.getActionSender().sendInventory();
                        player.getActionSender().sendEquipmentStats();
                        long now = System.currentTimeMillis();
                        player.save();
                        player.setLastSaveTime(now);
                        affectedPlayer.save();
                        affectedPlayer.setLastSaveTime(now);
                        player.getActionSender().sendMessage("Trade completed.");

                        affectedPlayer.getActionSender().sendInventory();
                        affectedPlayer.getActionSender().sendEquipmentStats();
                        affectedPlayer.getActionSender().sendMessage("Trade completed.");

                        unsetOptions(player);
                        unsetOptions(affectedPlayer);

                    }
                    break;
                case 216: // Trade declined
                    affectedPlayer = player.getWishToTrade();
                    if (affectedPlayer == null || busy(affectedPlayer) || !player.isTrading() || !affectedPlayer.isTrading()) { // This
                        // shouldn't
                        // happen
                        player.setSuspiciousPlayer(true);
                        unsetOptions(player);
                        unsetOptions(affectedPlayer);
                        return;
                    }
                    affectedPlayer.getActionSender().sendMessage(player.getUsername() + " has declined the trade.");

                    unsetOptions(player);
                    unsetOptions(affectedPlayer);
                    break;
                case 70: // Receive offered item data
                    affectedPlayer = player.getWishToTrade();
                    if (affectedPlayer == null || busy(affectedPlayer) || !player.isTrading() || !affectedPlayer.isTrading() || (player.isTradeOfferAccepted() && affectedPlayer.isTradeOfferAccepted()) || player.isTradeConfirmAccepted() || affectedPlayer.isTradeConfirmAccepted()) { // This
                        // shouldn't
                        // happen
                        player.setSuspiciousPlayer(true);
                        unsetOptions(player);
                        unsetOptions(affectedPlayer);
                        return;
                    }

                    player.setTradeOfferAccepted(false);
                    player.setTradeConfirmAccepted(false);
                    affectedPlayer.setTradeOfferAccepted(false);
                    affectedPlayer.setTradeConfirmAccepted(false);

                    // player.getActionSender().sendTradeAcceptUpdate();
                    // affectedPlayer.getActionSender().sendTradeAcceptUpdate();

                    Inventory tradeOffer = new Inventory();
                    player.resetTradeOffer();
                    int count = (int) p.readByte();
                    for (int slot = 0; slot < count; slot++) {
                        InvItem tItem = new InvItem(p.readShort(), p.readInt());
                        if (tItem.getAmount() < 1) {
                            player.setSuspiciousPlayer(true);
                            player.setRequiresOfferUpdate(true);
                            continue;
                        }
                        
                        ItemDef def = tItem.getDef();
                        if (!def.canTrade()) {
                            player.getActionSender().sendMessage(def.getName() + " cannot be traded with other players");
                            player.setRequiresOfferUpdate(true);
                            continue;
                        } 
                        tradeOffer.add(tItem);
                    }
                    for (InvItem item : tradeOffer.getItems()) {
                        if (tradeOffer.countId(item.getID()) > player.getInventory().countId(item.getID())) {
                            player.setSuspiciousPlayer(true);
                            unsetOptions(player);
                            unsetOptions(affectedPlayer);
                            return;
                        }
                        player.addToTradeOffer(item);
                    }
                    player.setRequiresOfferUpdate(true);
                    break;
            } 
        } catch (Exception e) {
        	//e.printStackTrace(); 
        }
    }

    private void unsetOptions(Player p) {
        if (p == null) {
            return;
        }
        p.resetTrading();
    }

}
