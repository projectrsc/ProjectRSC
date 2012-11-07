package org.darkquest.gs.phandler.client;

import org.darkquest.config.Constants;
import org.darkquest.config.Formulae;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.event.ShortEvent;
import org.darkquest.gs.event.impl.WalkToPointEvent;
import org.darkquest.gs.external.EntityHandler;
import org.darkquest.gs.external.FiremakingDef;
import org.darkquest.gs.model.Bubble;
import org.darkquest.gs.model.GameObject;
import org.darkquest.gs.model.InvItem;
import org.darkquest.gs.model.Item;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.model.Point;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.states.Action;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.world.ActiveTile;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

public class InvUseOnGroundItem implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private Item getItem(int id, ActiveTile tile, Player player) {
        for (Item i : tile.getItems()) {
            if (i.getID() == id && i.visibleTo(player)) {
                return i;
            }
        }
        return null;
    }

    public void handlePacket(Packet p, Channel session) throws Exception {
        try {
            Player player = (Player) session.getAttachment();
            if (player.isBusy()) {
                player.resetPath();
                return;
            }

            player.resetAll();
            Point location = Point.location(p.readShort(), p.readShort());
            int id = p.readShort();
            final ActiveTile tile = world.getTile(location);
            if (tile == null)
                return;
            final InvItem myItem = player.getInventory().get(p.readShort());
            if (myItem == null)
                return;

            if (tile.hasGameObject() && myItem.getID() != 135) {
                player.getActionSender().sendMessage("You cannot do that here, please move to a new area.");
                return;
            }
            final Item item = getItem(id, tile, player);

            if (item == null || myItem == null) {
                player.setSuspiciousPlayer(true);
                player.resetPath();
                return;
            }
            
            //Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " used item " + myItem + " at " + player.getLocation()));
            
            player.setStatus(Action.USING_INVITEM_ON_GITEM);
            World.getWorld().getDelayedEventHandler().add(new WalkToPointEvent(player, location, 1, false) {
                public void arrived() {
                    if (owner.isBusy() || owner.isRanging() || !tile.hasItem(item) || !owner.nextTo(item) || owner.getStatus() != Action.USING_INVITEM_ON_GITEM) {
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
                                        if (tile.hasGameObject() && tile.getGameObject().equals(fire)) {
                                            world.unregisterGameObject(fire);
                                            world.registerItem(new Item(181, tile.getX(), tile.getY(), 1, null));
                                        }
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

}
