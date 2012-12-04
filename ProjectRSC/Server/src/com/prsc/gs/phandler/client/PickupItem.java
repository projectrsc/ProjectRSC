package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.event.impl.FightEvent;
import com.prsc.gs.event.impl.WalkToPointEvent;
import com.prsc.gs.model.ChatMessage;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Point;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.states.Action;
import com.prsc.gs.world.ActiveTile;
import com.prsc.gs.world.World;


public class PickupItem implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private Item getItem(int id, ActiveTile tile, Player player) {
        if (tile != null)
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
        final Item item = getItem(id, tile, player);
        if (item == null) {
            player.resetPath();
            return;
        }

        player.setStatus(Action.TAKING_GITEM);
        World.getWorld().getDelayedEventHandler().add(new WalkToPointEvent(player, location, 1, false) {
            public void arrived() {
                if (owner.isBusy() || owner.isRanging() || !tile.hasItem(item) || !owner.nextTo(item) || owner.getStatus() != Action.TAKING_GITEM) {
                    return;
                }
                
                if(item.getDef().members && !Constants.GameServer.MEMBER_WORLD) {
                	owner.sendMemberErrorMessage();
                	return;
                }
                if (item.getID() == 23) {
                    owner.getActionSender().sendMessage("I can't pick it up!");
                    owner.getActionSender().sendMessage("I need a pot to hold it in.");
                    return;
                }
                owner.resetAll();
                InvItem invItem = new InvItem(item.getID(), item.getAmount());
                if (!owner.getInventory().canHold(invItem)) {
                    //owner.getActionSender().sendMessage("You cannot pickup this item, your inventory is full!");
                    return;
                }
                try {
                    if (item.getID() == 59 && item.getX() == 106 && item.getY() == 1476) {
                        Npc n = world.getNpc(37, 103, 107, 1476, 1479);
                        if (n != null && !n.inCombat()) {
                            owner.informOfNpcMessage(new ChatMessage(n, "Nobody steals from this gang!", owner));
                            fight(owner, n);
                        }
                    } 
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
        		//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(owner.getUsername() + " picked up " + item + " at " + owner.getLocation()));

                if (PluginHandler.getPluginHandler().blockDefaultAction("Pickup", new Object[]{owner, item})) {
                    return;
                }

                world.unregisterItem(item);
                owner.getActionSender().sendSound("takeobject");
                owner.getInventory().add(invItem);
                owner.getActionSender().sendInventory();
            }
        });
    } catch (Exception e) {
	return;
	}
}

    void fight(Player owner, Npc n) {
        n.resetPath();
        owner.resetPath();
        owner.resetAll();
        owner.setStatus(Action.FIGHTING_MOB);
        owner.getActionSender().sendSound("underattack");
        owner.getActionSender().sendMessage("You are under attack!");

        n.setLocation(owner.getLocation(), true);
        for (Player p : n.getViewArea().getPlayersInView()) {
            p.removeWatchedNpc(n);
        }

        owner.setBusy(true);
        owner.setSprite(9);
        owner.setOpponent(n);
        owner.setCombatTimer();

        n.setBusy(true);
        n.setSprite(8);
        n.setOpponent(owner);
        n.setCombatTimer();
        FightEvent fighting = new FightEvent(owner, n, true);
        fighting.setLastRun(0);
        World.getWorld().getDelayedEventHandler().add(fighting);
    }

}
