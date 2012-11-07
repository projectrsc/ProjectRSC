package org.darkquest.gs.phandler.client;

import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;

import org.darkquest.config.Formulae;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.event.impl.WalkToMobEvent;
import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.gs.states.Action;
import org.darkquest.gs.util.Logger;
import org.darkquest.gs.util.Script;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

public final class TalkToNpcHandler implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		if (System.currentTimeMillis() - player.lastNPCChat < 1500)
			return;
		player.lastNPCChat = System.currentTimeMillis();
		player.resetAll();
		final Npc npc = world.getNpc(p.readShort());
		if (npc == null) {
			return;
		}

		//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " talked to npc " + npc.getDef().getName() + " at " + player.getLocation()));

		player.setFollowing(npc);
		player.setStatus(Action.TALKING_MOB);
		World.getWorld().getDelayedEventHandler().add(new WalkToMobEvent(player, npc, 1) {
			public void arrived() {
				owner.resetFollowing();
				owner.resetPath();
				if (owner.isBusy() || owner.isRanging() || !owner.nextTo(npc) || owner.getStatus() != Action.TALKING_MOB) {
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
				
				if (world.npcScripts.containsKey(npc.getID())) {
					dir = Formulae.getDirection(owner, npc);
					if (dir != -1) {
						if(world.npcScripts.containsKey(npc.getID())) {
							owner.setSprite(Formulae.getDirection(npc, owner));
							npc.setSprite(Formulae.getDirection(owner, npc));
						}	
					}
					owner.setBusy(true);
					npc.blockedBy(owner);
					if (owner.interpreterThread != null) {
						try {
							owner.interpreterThread.stop();
						} catch (Exception e) {

						}
					}

					owner.interpreterThread = new Thread(new Runnable() {
						public void run() {
							try {
								try {
									new Script(owner, npc);
								} catch (ConcurrentModificationException cme) {
									Logger.println("CME (Ignore This): " + owner.getUsername());
								} catch(Exception e) {

								}
								owner.setBusy(false);

								npc.unblock();
							} catch (Exception e) {
								if (!(e instanceof InvocationTargetException)) {
									e.printStackTrace();
								}
	
								npc.unblock();
								owner.setBusy(false);
							}
						}
					});
					owner.interpreterThread.start();
				} else if (PluginHandler.getPluginHandler().blockDefaultAction("TalkToNpc", new Object[]{owner, npc})) {
				
					return;
				} else {
					dir = Formulae.getDirection(owner, npc);
					if (dir != -1) {
						if(world.npcScripts.containsKey(npc.getID())) {
							owner.setSprite(Formulae.getDirection(npc, owner));
							npc.setSprite(Formulae.getDirection(owner, npc));
						}	
					}
				}
			}
		});
	}
}
