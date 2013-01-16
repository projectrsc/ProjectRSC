package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class CommandHandler implements PacketHandler {

	public void handlePacket(Packet p, Channel channel) throws Exception {
		Client client = (Client) channel.getAttachment();
		Player player = client.getPlayer();
		player.resetAll();
		String s = new String(p.getData()).trim();
		int firstSpace = s.indexOf(" ");
		String cmd = s;
		String[] args = new String[0];
		if (firstSpace != -1) {
			cmd = s.substring(0, firstSpace).trim();
			args = s.substring(firstSpace + 1).trim().split(" ");
		}
		PluginHandler.getPluginHandler().handleAction("Command", new Object[]{cmd.toLowerCase(), args, player});
	}

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{90};
	}
}
