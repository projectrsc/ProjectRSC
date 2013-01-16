package com.prsc.gs.plugins.phandler.client;


import org.jboss.netty.channel.Channel;

import com.prsc.gs.Server;
import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.MenuHandler;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.plugins.task.Task;


public class MenuReplyHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Channel channel) throws Exception {
		try {
			Client client = (Client) channel.getAttachment();
			Player player = client.getPlayer();
			
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
			
			Server.getInstance().getTaskManager().submitTask(new Task() {
				
				@Override
				public void run() {
					menuHandler.handleReply(option, reply);
				}
				
			});
			/*
			FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					try {
						menuHandler.handleReply(option, reply);
					} catch (ConcurrentModificationException cme) {
						cme.printStackTrace();
					}
					return 1;
				}
			});
			PluginHandler.getPluginHandler().getWorkers().execute(task); */
			//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " answered a menu question (" + option + ") at " + player.getLocation()));
		} catch (Exception e) { e.printStackTrace();}
	}

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{154};
	}
}
