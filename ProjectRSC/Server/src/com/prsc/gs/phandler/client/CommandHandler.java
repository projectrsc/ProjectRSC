package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;

public final class CommandHandler implements PacketHandler {

	public void handlePacket(Packet p, Channel session) throws Exception {
		Player player = (Player) session.getAttachment();
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
}
