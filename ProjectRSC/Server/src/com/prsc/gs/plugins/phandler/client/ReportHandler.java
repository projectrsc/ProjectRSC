
package com.prsc.gs.plugins.phandler.client;

import org.jboss.netty.channel.Channel;

import com.prsc.gs.connection.Client;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.core.GameEngine;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;

public final class ReportHandler implements PacketHandler {

	public void handlePacket(Packet p, Channel channel) throws Exception {
		Client client = (Client) channel.getAttachment();
		Player player = client.getPlayer();

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

		//Services.lookup(DatabaseManager.class).addQuery(new GameReport(player.getUsername(), DataConversions.hashToUsername(hash), reason, mute));
	
		if (mute) {
			reported.setMuteTime((GameEngine.getAccurateTimestamp() / 1000) + 172800);
		}
		player.getActionSender().sendMessage("Thank-you your abuse report has been received.");    
	}

	@Override
	public int[] getAssociatedIdentifiers() {
		return new int[]{13};
	}

}

