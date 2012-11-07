package org.darkquest.gs.builders.ls;

import org.darkquest.config.Constants;
import org.darkquest.gs.builders.LSPacketBuilder;
import org.darkquest.gs.connection.LSPacket;
import org.darkquest.gs.connection.Packet;
import org.darkquest.gs.core.LoginConnector;
import org.darkquest.gs.event.DelayedEvent;
import org.darkquest.gs.model.Player;
import org.darkquest.gs.model.Point;
import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.phandler.PlayerLogin;
import org.darkquest.gs.tools.DataConversions;
import org.darkquest.gs.util.EntityList;
import org.darkquest.gs.util.Logger;
import org.darkquest.gs.world.World;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MiscPacketBuilder {

	private final LoginConnector connector;

	private List<LSPacket> packets = Collections.synchronizedList(new ArrayList<LSPacket>());
	private final Object lock = new Object();

	private final World world = World.getWorld();

	public MiscPacketBuilder(LoginConnector connector) {
		this.connector = connector;
	}

	private void queuePacket(LSPacket packet) {
		synchronized (lock) {
			packets.add(packet);
		}
	}

	/**
	 * Gets a List of new packets since the last update
	 */
	public List<LSPacket> getPackets() {
		synchronized (lock) {
			List<LSPacket> temp = this.packets;
			this.packets = Collections.synchronizedList(new ArrayList<LSPacket>(temp.size()));//new ArrayList<LSPacket>(temp.size());

			return temp;
		}
	}

	public void addFriend(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(11);
		s.addLong(user);
		s.addLong(friend);
		queuePacket(s.toPacket());
	}

	public void addIgnore(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(13);
		s.addLong(user);
		s.addLong(friend);
		queuePacket(s.toPacket());
	}

	public void banPlayer(final Player mod, final long user, final boolean ban) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(ban ? 4 : 5);
		s.addLong(user);
		final long modhash = DataConversions.usernameToHash(mod.getUsername());
		s.addLong(modhash);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, Channel session)
					throws Exception {
				if (p.readByte() == 1) {
					Logger.mod(mod.getUsername() + " "
							+ (ban ? "banned" : "unbanned") + " "
							+ DataConversions.hashToUsername(user));
				}
				if(ban) {
					Player pp = world.getPlayer(user);
					if(pp != null)
						pp.destroy(true);
				}
				mod.getActionSender().sendMessage(p.readString());

			}
		});
		queuePacket(s.toPacket());
	}

	public void logAction(String message, int type) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(32);
		s.addByte((byte) type);
		s.addBytes(message.getBytes());
		queuePacket(s.toPacket());
	}

	public void playerLogin(Player player) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(31);
		s.setHandler(connector, new PlayerLogin(player));
		s.addLong(player.getUsernameHash());
		s.addLong(DataConversions.IPToLong(player.getCurrentIP()));
		s.addBytes(player.getPassword().getBytes());
		//s.addBytes(DataConversions.sha1(player.getPassword()).getBytes());
		queuePacket(s.toPacket());
	}

	public static int PACKETS = 0;
	public void playerLogout(final long user) {
					LSPacketBuilder s = new LSPacketBuilder();
					s.setID(30);
					s.addLong(user);
					s.addLong(user);
					queuePacket(s.toPacket());
		
	}

	/**
	 * Tells the login server we are registered and lists players connected
	 * (should be 0 at startup)
	 */
	public void registerWorld() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(1);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, Channel session) throws Exception {
				connector.setRegistered(p.readByte() == 1);
			}
		});
		s.addShort(Constants.GameServer.WORLD_NUMBER);
		s.addByte((byte) (Constants.GameServer.MEMBER_WORLD ? 1 : 0));
		s.addBytes(DataConversions.formatString(Constants.GameServer.SERVER_LOCATION, 30).getBytes());

		EntityList<Player> players = world.getPlayers();
		s.addShort(players.size());
		for (Player player : players) {
			s.addLong(player.getUsernameHash());
			s.addLong(DataConversions.IPToLong(player.getCurrentIP()));
		}
		queuePacket(s.toPacket());
	}

	public void removeFriend(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(12);
		s.addLong(user);
		s.addLong(friend);
		queuePacket(s.toPacket());
	}

	public void removeIgnore(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(14);
		s.addLong(user);
		s.addLong(friend);
		queuePacket(s.toPacket());
	}

	public void reportUser(long user, long reported, byte reason) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(15);
		s.addLong(user);
		s.addLong(reported);
		s.addByte(reason);
		queuePacket(s.toPacket());
	}

	public void requestPlayerInfo(final Player mod, final long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(8);
		s.addLong(user);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, Channel session)
					throws Exception {
				if (p.readByte() == 1) {
					Logger.mod(mod.getUsername() + " requested information on " + DataConversions.hashToUsername(user));

					int world = p.readShort();
					Point location = Point.location(p.readShort(), p.readShort());
					long loginDate = p.readLong();
					int lastMoved = (int) ((System.currentTimeMillis() - p.readLong()) / 1000);
					boolean chatBlock = p.readByte() == 1;
					int fatigue = p.readShort();
					String state = p.readString();

					mod.getActionSender().sendAlert(
							"@whi@" + DataConversions.hashToUsername(user)
							+ " is currently on world @or1@" + world
							+ "@whi@ at @or1@" + location.toString()
							+ "@whi@ (@or1@"
							+ location.getDescription()
							+ "@whi@). State is @or1@" + state
							+ "@whi@. Logged in @or1@"
							+ DataConversions.timeSince(loginDate)
							+ "@whi@ ago. Last moved @or1@" + lastMoved
							+ " secs @whi@ ago. Chat block is @or1@"
							+ (chatBlock ? "on" : "off")
							+ "@whi@. Fatigue is at @or1@" + fatigue
							+ "@whi@.", false);
				} else {
					mod.getActionSender().sendMessage("Invalid player, maybe they aren't currently online?");
				}
			}
		});
		queuePacket(s.toPacket());
	}

	public void saveProfiles() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(9);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, Channel session)
					throws Exception {
				if (p.readByte() != 1) {
					Logger.error("Error saving all profiles!");
				}
			}
		});
		queuePacket(s.toPacket());
	}

	public void sendPM(long user, long friend, boolean avoidBlock, byte[] message) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(10);
		s.addLong(user);
		s.addLong(friend);
		s.addByte((byte) (avoidBlock ? 1 : 0));
		s.addBytes(message);
		queuePacket(s.toPacket());
	}

	public void unregisterWorld() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(2);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, Channel session) throws Exception {
				session.close().awaitUninterruptibly();
				World.getWorld().getServer().unbind();
				World.getWorld().getServer().getEngine().kill();
			}
		});
		queuePacket(s.toPacket());
	}
}
