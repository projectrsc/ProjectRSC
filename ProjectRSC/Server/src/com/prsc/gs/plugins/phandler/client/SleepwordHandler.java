package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;


import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.connection.RSCPacket;
import com.prsc.gs.event.SingleEvent;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class SleepwordHandler implements PacketHandler {

	public void handlePacket(Packet p, Channel channel) throws Exception {
		Client client = (Client) channel.getAttachment();
		Player player = client.getPlayer();

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

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{200};
	}
}
