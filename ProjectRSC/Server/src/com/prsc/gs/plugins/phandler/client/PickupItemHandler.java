package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.config.Constants;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.event.impl.FightEvent;
import com.prsc.gs.event.impl.WalkToPointEvent;
import com.prsc.gs.model.ChatMessage;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Point;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.states.Action;


public class PickupItemHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = World.getWorld();

    private Item getItem(int id, Point location, Player player) { // ActiveTile tile,
    	int x = location.getX();
    	int y = location.getY();
    	for(Item i : player.getViewArea().getItemsInView()) {
    		if (i.getID() == id && i.visibleTo(player) && i.getX() == x && i.getY() == y) {
                return i;
            }
    	} 
    	System.out.println("item is in view: null");
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
        //final ActiveTile tile = world.getTile(location);
        final Item item = getItem(id, location, player); // tile
        if (item == null) {
            player.resetPath();
            return;
        }

        player.setStatus(Action.TAKING_GITEM);
        World.getWorld().getDelayedEventHandler().add(new WalkToPointEvent(player, location, 1, false) {
            public void arrived() { // || !tile.hasItem(item)
            	/*List<Item> items = Lists.newArrayList(owner.getCurrentArea().getItems());
            	
            	if(!items.contains(item)) { // if the item defined does not exist within the player's region
            		return;
            	} */
            	//!owner.nextTo(item)
                if (owner.isBusy() || owner.isRanging() || !owner.withinRange(item, 1) || owner.getStatus() != Action.TAKING_GITEM) {
                	System.out.println("Returned");
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
	} // wow xEnt...
    }

    private void fight(Player owner, Npc n) {
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

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{245};
	}

}
