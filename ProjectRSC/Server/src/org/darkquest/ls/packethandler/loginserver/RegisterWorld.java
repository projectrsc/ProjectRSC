package org.darkquest.ls.packethandler.loginserver;

import org.darkquest.ls.Server;
import org.darkquest.ls.model.World;
import org.darkquest.ls.net.LSPacket;
import org.darkquest.ls.net.Packet;
import org.darkquest.ls.packetbuilder.loginserver.WorldRegisteredPacketBuilder;
import org.darkquest.ls.packethandler.PacketHandler;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;

public final class RegisterWorld implements PacketHandler {

	private final WorldRegisteredPacketBuilder builder = new WorldRegisteredPacketBuilder();

	public void handlePacket(Packet p, Channel session) throws Exception {
		final long uID = ((LSPacket) p).getUID();
		try {
			builder.setUID(uID);
			builder.setSuccess(false);
			
			Server server = Server.getServer();
			if (((LSPacket) p).getID() == 1) {
				int id = p.readShort();
				if(id != 1) {// LS not accepting login fix?
					System.out.println("Attemped to register world: " + id + " - but we blocked it!");
					return;
				}
				boolean members = p.readByte() == 1;
				String location = p.readString(30).trim();

				if (server.getWorld(id) == null) {
					World world = server.getIdleWorld(id);
					if (world == null) {
						world = new World(id, members, location, session);
						server.registerWorld(world);
						System.out.println("Registering world");
						System.out.println("\tid: " + id);
						System.out.println("\tmembers: " + members);
						int playerCount = p.readShort();
						for (int i = 0; i < playerCount; i++) {
							world.registerPlayer(p.readLong(), DataConversions.IPToString(p.readLong()));
						}

						session.setAttachment(world);
						builder.setSuccess(true);
					} else {
						world.setChannel(session);
						world.setMembers(members);
						world.setLocation(location);
						server.setIdle(world, false);
						System.out.println("Reattached world");
						System.out.println("\tid: " + id + " members: " + members);
						int playerCount = p.readShort();
						for (int i = 0; i < playerCount; i++) {
							world.registerPlayer(p.readLong(), DataConversions.IPToString(p.readLong()));
						}

						session.setAttachment(world);
						builder.setSuccess(true);
					}

			
				}
			} else {
				World world = (World) session.getAttachment();

				server.unregisterWorld(world);
				System.out.println("UnRegistering world: " + world.getID());
				session.setAttachment(null);
				builder.setSuccess(true);
			}

			LSPacket temp = builder.getPacket();

			if (temp != null) {
				session.write(temp);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
