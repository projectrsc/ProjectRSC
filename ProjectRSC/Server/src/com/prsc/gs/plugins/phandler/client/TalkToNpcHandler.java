package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.config.Formulae;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.core.GameEngine;
import com.prsc.gs.event.impl.WalkToMobEvent;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.states.Action;

public final class TalkToNpcHandler implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel channel) throws Exception {
		Client client = (Client) channel.getAttachment();
		Player player = client.getPlayer();
		
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		
		if (GameEngine.getAccurateTimestamp() - player.lastNPCChat < 1500)
			return;
		
		player.lastNPCChat = GameEngine.getAccurateTimestamp();
		player.resetAll();
		
		Npc n = null;
		
		try {
			n = world.getNpc(p.readShort());
			if (n == null) {
				return;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} 
		
		final Npc npc = n;
		//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " talked to npc " + npc.getDef().getName() + " at " + player.getLocation()));

		player.setFollowing(npc);
		player.setStatus(Action.TALKING_MOB);
		World.getWorld().getDelayedEventHandler().add(new WalkToMobEvent(player, npc, 1) {
			public void arrived() {
				owner.resetFollowing();
				owner.resetPath(); //|| !owner.nextTo(npc)
				if (owner.isBusy() || owner.isRanging() || !owner.withinRange(npc, 1) || owner.getStatus() != Action.TALKING_MOB) {
					return;
				}
				owner.resetAll();
				if (npc.isBusy()) {
					owner.getActionSender().sendMessage(npc.getDef().getName() + " is currently busy.");
					return;
				}
				npc.resetPath();

				
				int dir = Formulae.getDirection(owner, npc);
				if (dir != -1) {
					if(handler != null) {
						owner.setSprite(Formulae.getDirection(npc, owner));
						npc.setSprite(Formulae.getDirection(owner, npc));
					}	
				} 
				if (PluginHandler.getPluginHandler().blockDefaultAction("TalkToNpc", new Object[]{owner, npc})) {
					return;
				}
			}
		});
	}

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{177};
	}
}
