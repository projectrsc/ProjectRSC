package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.event.MiniEvent;
import com.prsc.gs.event.impl.FightEvent;
import com.prsc.gs.event.impl.WalkMobToMobEvent;
import com.prsc.gs.model.Mob;
import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Path;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.states.Action;
import com.prsc.gs.states.CombatState;
import com.prsc.gs.world.World;


public class WalkRequest implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel session) throws Exception {
		try {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();

		if (player.inCombat()) {
			if (pID == 132) {
				Mob opponent = player.getOpponent();
				if (opponent == null) { // This shouldn't happen
					player.setSuspiciousPlayer(true);
					return;
				}
				if (opponent.getHitsMade() >= 3) {
					if (player.isDueling() && player.getDuelSetting(0)) {
						player.getActionSender().sendMessage("Running has been disabled in this duel.");
						return;
					}
					player.setLastRun(System.currentTimeMillis());
					player.resetCombat(CombatState.RUNNING);

					player.isMining(false);
					if (opponent instanceof Npc) {
						Npc n = (Npc) opponent;
						n.unblock();
						opponent.resetCombat(CombatState.WAITING);
						if (n.getDef().aggressive || n.getLocation().inWilderness()) {
							player.lastNpcChasingYou = n;
							World.getWorld().getDelayedEventHandler().add(new MiniEvent(player, 2000) {
								public void action() {

									final Npc npc = owner.lastNpcChasingYou;
									owner.lastNpcChasingYou = null;
									if (npc.isBusy() || npc.getChasing() != null)
										return;

									npc.resetPath();
									npc.setChasing(owner);

									World.getWorld().getDelayedEventHandler().add(new WalkMobToMobEvent(npc, owner, 0) {
										public void arrived() {
											if (affectedMob.isBusy() || owner.isBusy()) {
												npc.setChasing(null);
												return;
											}
											if (affectedMob.inCombat() || owner.inCombat()) {
												npc.setChasing(null);
												return;
											}
											Player player = (Player) affectedMob;
											player.resetPath();
											player.setBusy(true);
											npc.resetPath();
											player.resetAll();
											player.setStatus(Action.FIGHTING_MOB);
											player.getActionSender().sendSound("underattack");
											player.getActionSender().sendMessage("You are under attack!");

											npc.setLocation(player.getLocation(), true);
											for (Player p : npc.getViewArea().getPlayersInView())
												p.removeWatchedNpc(npc);
											player.setBusy(true);
											player.setSprite(9);
											player.setOpponent(npc);
											player.setCombatTimer();

											npc.setBusy(true);
											npc.setSprite(8);
											npc.setOpponent(player);
											npc.setCombatTimer();
											npc.setChasing(null);
											FightEvent fighting = new FightEvent(player, npc, true);
											fighting.setLastRun(0);
											world.getDelayedEventHandler().add(fighting);
										}

										public void failed() {
											npc.setChasing(null);
										}
									});
								}
							});
						}

					} else {
						opponent.resetCombat(CombatState.WAITING);
					}
				} else {
					player.getActionSender().sendMessage("You cannot retreat in the first 3 rounds of battle.");
					return;
				}
			} else {
				return;
			}
		}// else if (player.isBusy() && System.currentTimeMillis() - player.lastMineTimer > 2000) {
		// return;
		//}

		if (player.isBusy()) {
			return;
		}
		if (System.currentTimeMillis() - player.lastCast < 600)
			return;

		player.isMining(false);
		player.resetAll();

		int startX = p.readShort();
		int startY = p.readShort();
		int numWaypoints = p.remaining() / 2;

		byte[] waypointXoffsets = new byte[numWaypoints];
		byte[] waypointYoffsets = new byte[numWaypoints];
		for (int x = 0; x < numWaypoints; x++) {
			waypointXoffsets[x] = p.readByte();
			waypointYoffsets[x] = p.readByte();
		}
		Path path = new Path(startX, startY, waypointXoffsets, waypointYoffsets);
		if(waypointXoffsets.length >= 1) {
			int endX = (int)waypointXoffsets[waypointXoffsets.length-1] + startX;
			int endY = (int)waypointYoffsets[waypointYoffsets.length-1] + startY;
			if(player.blink()) {
				player.teleport(endX, endY, false);
				return;
			}
		}



		player.setStatus(Action.IDLE);
		player.setPath(path);
	} catch (Exception e) {
        }
	}

}
