package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.config.Constants;
import com.prsc.config.Formulae;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.event.ShortEvent;
import com.prsc.gs.event.impl.WalkToPointEvent;
import com.prsc.gs.external.EntityHandler;
import com.prsc.gs.external.FiremakingDef;
import com.prsc.gs.model.Bubble;
import com.prsc.gs.model.GameObject;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Point;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.states.Action;
import com.prsc.gs.tools.DataConversions;

public class InvUseOnGroundItemHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private Item getItem(int id, Player player) { // ActiveTile tile
        for (Item i : player.getViewArea().getItemsInView()) {
            if (i.getID() == id && i.visibleTo(player)) {
                return i;
            }
        } 
        return null;
    }

    public void handlePacket(Packet p, Channel channel) throws Exception {
        try {
            Client client = (Client) channel.getAttachment();
            Player player = client.getPlayer();
            
            if (player.isBusy()) {
                player.resetPath();
                return;
            }

            player.resetAll();
            Point location = Point.location(p.readShort(), p.readShort());
            int id = p.readShort();
            /*
            final ActiveTile tile = world.getTile(location);
            if (tile == null)
                return; */
            final InvItem myItem = player.getInventory().get(p.readShort());
            if (myItem == null)
                return;
            /*
            if (tile.hasGameObject() && myItem.getID() != 135) {
                player.getActionSender().sendMessage("You cannot do that here, please move to a new area.");
                return;
            } */
            final Item item = getItem(id, player);

            if (item == null || myItem == null) {
                player.setSuspiciousPlayer(true);
                player.resetPath();
                return;
            }
            
            //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used item " + myItem + " at " + player.getLocation()));
            
            player.setStatus(Action.USING_INVITEM_ON_GITEM);
            World.getWorld().getDelayedEventHandler().add(new WalkToPointEvent(player, location, 1, false) {
                public void arrived() { //|| !tile.hasItem(item) !owner.nextTo(item)
                    if (owner.isBusy() || owner.isRanging() || !owner.withinRange(item, 1) || owner.getStatus() != Action.USING_INVITEM_ON_GITEM) {
                        return;
                    }
                    if (myItem == null || item == null)
                        return;

                    if ((myItem.getDef().isMembers() || item.getDef().isMembers()) && !Constants.GameServer.MEMBER_WORLD) {
                        owner.getActionSender().sendMessage("Nothing interesting happens.");
                        return;
                    }

                    if (PluginHandler.getPluginHandler().blockDefaultAction("InvUseOnGroundItem", new Object[]{myItem, item, owner})) {
                        return;
                    }

                    switch (item.getID()) {
                        case 23:
                            if (myItem.getID() == 135) {
                                if (owner.getInventory().remove(myItem) < 0)
                                    return;
                                owner.getActionSender().sendMessage("You put the flour in the pot.");
                                Bubble bubble = new Bubble(owner, 135);
                                for (Player p : owner.getViewArea().getPlayersInView()) {
                                    p.informOfBubble(bubble);
                                }
                                world.unregisterItem(item);
                                owner.getInventory().add(new InvItem(136));
                                owner.getActionSender().sendInventory();
                                return;
                            }
                        case 14:
                        case 632:
                        case 633:
                        case 634:
                        case 635:
                        case 636:
                            handleFireMaking();
                            break;
                        default:
                            owner.getActionSender().sendMessage("Nothing interesting happens.");
                            return;
                    }
                }

                private void handleFireMaking() {
                    handleFireMaking((int) Math.ceil(owner.getMaxStat(11) / 10));

                }

                private void handleFireMaking(int tries) {
                    final int retries = --tries;
                    final FiremakingDef def = EntityHandler.getFiremakingDef(item.getID());
                    if (!itemId(new int[]{166}) || def == null) {
                        owner.getActionSender().sendMessage("Nothing interesting happens.");
                        return;
                    }
                    if (owner.getCurStat(11) < def.getRequiredLevel()) {
                        owner.getActionSender().sendMessage("You need at least " + def.getRequiredLevel() + " firemaking to light these logs.");
                        return;
                    }
                    owner.setBusy(true);
                    Bubble bubble = new Bubble(owner, 166);
                    for (Player p : owner.getViewArea().getPlayersInView()) {
                        p.informOfBubble(bubble);
                    }
                    owner.getActionSender().sendMessage("You attempt to light the logs...");
                    World.getWorld().getDelayedEventHandler().add(new ShortEvent(owner) {
                        public void action() {
                            if (Formulae.lightLogs(def, owner.getCurStat(11))) {
                                owner.getActionSender().sendMessage("They catch fire and start to burn.");
                                world.unregisterItem(item);
                                final GameObject fire = new GameObject(item.getLocation(), 97, 0, 0);
                                world.registerGameObject(fire);
                                world.getDelayedEventHandler().add(new DelayedEvent(null, def.getLength()) {
                                    public void run() {
                                    	/*
                                        if (tile.hasGameObject() && tile.getGameObject().equals(fire)) {
                                            world.unregisterGameObject(fire);
                                            world.registerItem(new Item(181, tile.getX(), tile.getY(), 1, null));
                                        } */
                                        matchRunning = false;
                                    }
                                });
                                owner.incExp(11, Formulae.firemakingExp(owner.getMaxStat(11), def.getExp()), true);
                                owner.getActionSender().sendStat(11);
                                owner.setBusy(false);
                            } else {
                                owner.getActionSender().sendMessage("You fail to light them.");
                                owner.setBusy(false);
                                if (retries > 0) {
                                    handleFireMaking(retries);
                                }
                            }

                        }
                    });
                }

                private boolean itemId(int[] ids) {
                    return DataConversions.inArray(ids, myItem.getID());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{34};
	}

}
