package com.prsc.gs.phandler.client;


import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.event.DelayedEvent;
import com.prsc.gs.event.SingleEvent;
import com.prsc.gs.model.InvItem;
import com.prsc.gs.model.Item;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.states.Action;
import com.prsc.gs.world.World;

public final class DropHandler implements PacketHandler {

	public void handlePacket(Packet p, Channel session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final int idx = (int) p.readShort();
		if (idx < 0 || idx >= player.getInventory().size()) {
			player.setSuspiciousPlayer(true);
			return;
		}
		final InvItem item = player.getInventory().get(idx);
		if (item == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		
		if (PluginHandler.getPluginHandler().blockDefaultAction("Drop", new Object[]{player, item})) {
            return;
        }

		// drop item after a path has finished
		if(player.pathHandler != null && !player.pathHandler.finishedPath()) {
			waitAndDrop(player, item);
		} else {
			drop(player, item);
		}



	}

	public void waitAndDrop(final Player player,final InvItem item) {
		World.getWorld().getDelayedEventHandler().add(new SingleEvent(player, 500) {

			@Override
			public void action() {
				if(owner.dropTickCount > 20) { // 10 seconds they are allowed to walk for. anything longer won't drop.
					owner.dropTickCount = 0;
					stop();
				} else {
					owner.dropTickCount++;
					if(owner.pathHandler != null && !owner.pathHandler.finishedPath()) {
						waitAndDrop(owner, item);
					} else {
						drop(owner, item);
					}
				}


			}
		});

	}

	public void drop(Player player, final InvItem item) {
		player.setStatus(Action.DROPPING_GITEM);
		World.getWorld().getDelayedEventHandler().add(new DelayedEvent(player, 500) {
			public void run() {
				if (owner.isBusy() || !owner.getInventory().contains(item)
						|| owner.getStatus() != Action.DROPPING_GITEM) {
					matchRunning = false;
					return;
				}
				if (owner.hasMoved()) {
					this.stop();
					return;
				}
	
				owner.getActionSender().sendSound("dropobject");
				owner.getInventory().remove(item);
				owner.getActionSender().sendInventory();
				world.registerItem(new Item(item.getID(), owner.getX(), owner
						.getY(), item.getAmount(), owner));
				matchRunning = false;
			}
		});
	}
}