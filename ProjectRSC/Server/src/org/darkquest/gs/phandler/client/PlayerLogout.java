package org.darkquest.gs.phandler.client;

import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PluginHandler;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;

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
