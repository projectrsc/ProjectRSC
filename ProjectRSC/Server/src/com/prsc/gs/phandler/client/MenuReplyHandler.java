package com.prsc.gs.phandler.client;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.world.World;


public class MenuReplyHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel session) throws Exception {
		try {
			Player player = (Player) session.getAttachment();
			final MenuHandler menuHandler = player.getMenuHandler();
			if (menuHandler == null) {
				player.setSuspiciousPlayer(true);
				return;
			}
			byte readOption = p.readByte();  

			if (readOption < 20) {
				player.setBotDetected(true);
				readOption += 20;
			}

			final int option = (int) (readOption - 20);
			final String reply = option == 30 ? "NULL" : menuHandler.getOption(option);
			player.resetMenuHandler();
			if (reply == null) {
				player.setSuspiciousPlayer(true);
				return;
			}

			FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					try {
						menuHandler.handleReply(option, reply);
					} catch (java.util.ConcurrentModificationException cme) {
						cme.printStackTrace();
					}
					return 1;
				}
			});
			PluginHandler.getPluginHandler().getExecutor().execute(task);
			//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " answered a menu question (" + option + ") at " + player.getLocation()));
		} catch (Exception e) { e.printStackTrace();}
	}
}
