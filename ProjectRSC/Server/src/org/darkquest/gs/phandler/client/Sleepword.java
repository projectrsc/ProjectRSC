package org.darkquest.gs.phandler.client;

import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.connection.RSCPacket;
import org.darkquest.gs.event.SingleEvent;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

public final class Sleepword implements PacketHandler {

	public void handlePacket(Packet p, Channel channel) throws Exception {
		Player player = (Player) channel.getAttachment();

		try {
			String sleepword = ((RSCPacket) p).readString().trim();
			if (sleepword.equalsIgnoreCase("-null-")) {
				player.incrementSleepTries();

				World.getWorld().getDelayedEventHandler().add(new SingleEvent(player, player.getIncorrectSleepTimes() * 1000) {
					@Override
					public void action() {
						owner.getActionSender().sendEnterSleep();
					}
				});
			} else {
				if (!player.isSleeping()) { 
					return; 
				}
				if (sleepword.equalsIgnoreCase(player.getSleepword())) {
					player.getActionSender().sendWakeUp(true, false);
					player.resetSleepTries();
					//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " submitted a correct sleepword"));
				} else {
					player.getActionSender().sendIncorrectSleepword();
					player.incrementSleepTries();
					if(player.getIncorrectSleepTimes() > 5) {
						player.destroy(false);
						return;
					}

					World.getWorld().getDelayedEventHandler().add(new SingleEvent(player, player.getIncorrectSleepTimes() * 1000) {
						@Override
						public void action() {
							owner.getActionSender().sendEnterSleep();
						}
					});
					//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " failed a sleepword"));
				}
			}
		} catch (Exception e) {
			if (player.isSleeping()) {
				player.getActionSender().sendIncorrectSleepword();
				player.incrementSleepTries();
				if(player.getIncorrectSleepTimes() > 5) {
					player.destroy(false);
					return;
				}
				

				World.getWorld().getDelayedEventHandler().add(new SingleEvent(player, player.getIncorrectSleepTimes() * 1000) {
					@Override
					public void action() {
						owner.getActionSender().sendEnterSleep();
					}
				});	
			}
		}
	}
}
