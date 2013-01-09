package com.prsc.gs.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Packet;
import com.prsc.gs.model.Player;
import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.ls.util.DataConversions;

public final class PlayerLogout implements PacketHandler {

	public void handlePacket(Packet p, Channel session) throws Exception {
		Player player = (Player) session.getAttachment();
		
		if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerLogout", new Object[]{player}, false)) {
			player.getActionSender().sendCantLogout();
			return;
		}
		
		if (player.canLogout()) {
			player.destroy(true);
		} else {
			player.destroy(false);
		}
		
		//Services.lookup(DatabaseManager.class).addQuery(new GenericLog(player.getUsername() + " attempted to log out"));
	}
}
