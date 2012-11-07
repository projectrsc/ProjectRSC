
package org.darkquest.gs.phandler.client;

import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.db.DatabaseManager;
import org.darkquest.gs.db.query.GameReport;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.service.Services;
import org.darkquest.gs.world.World;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;

public final class ReportHandler implements PacketHandler {

	public void handlePacket(Packet p, Channel session) throws Exception {
		Player player = (Player) session.getAttachment();

		if (!player.canReport()) {
			player.getActionSender().sendMessage("You may only send one abuse report per minute.");
			return;
		}

		long hash = p.readLong();
		byte reason = p.readByte();
		boolean mute = p.readByte() == 1;

		if (mute && !player.isMod()) {
			mute = false;
			player.setSuspicious(true);
		} else if (reason < 0 || reason > 13) {
			player.setSuspicious(true);
		}

		Player reported = World.getWorld().getPlayer(hash);

		if (hash == player.getUsernameHash() || reported == null) {
			player.getActionSender().sendMessage("Invalid player.");
			return;
		}

		Services.lookup(DatabaseManager.class).addQuery(new GameReport(player.getUsername(), DataConversions.hashToUsername(hash), reason, mute));
	
		if (mute) {
			reported.setMuteTime((System.currentTimeMillis() / 1000) + 172800);
		}
		player.getActionSender().sendMessage("Thank-you your abuse report has been received.");    
	}

}

