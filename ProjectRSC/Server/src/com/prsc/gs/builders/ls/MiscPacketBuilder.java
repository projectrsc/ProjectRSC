package com.prsc.gs.builders.ls;

import org.jboss.netty.channel.Channel;



import com.prsc.config.Constants;
import com.prsc.gs.builders.LSPacketBuilder;
import com.prsc.gs.connection.Packet;
import com.prsc.gs.core.GameEngine;
import com.prsc.gs.core.LoginConnector;
import com.prsc.gs.model.Player;
import com.prsc.gs.model.Point;
import com.prsc.gs.model.World;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.plugins.phandler.PlayerLogin;
import com.prsc.gs.tools.DataConversions;
import com.prsc.gs.util.EntityList;
import com.prsc.gs.util.Logger;

public final class MiscPacketBuilder {

	private final LoginConnector connector;
	private final World world = World.getWorld();

	public MiscPacketBuilder(LoginConnector connector) {
		this.connector = connector;
	}

	public void addFriend(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(11);
		s.addLong(user);
		s.addLong(friend);
		connector.getSession().write(s.toPacket());
	}

	public void addIgnore(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(13);
		s.addLong(user);
		s.addLong(friend);
		connector.getSession().write(s.toPacket());
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

			@Override
			public int[] getAssociatedIdentifiers() {
				return new int[]{4,5};
			}
		});
		connector.getSession().write(s.toPacket());
	}

	public void logAction(String message, int type) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(32);
		s.addByte((byte) type);
		s.addBytes(message.getBytes());
		connector.getSession().write(s.toPacket());
	}

	public void playerLogin(Player player) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(31);
		s.setHandler(connector, new PlayerLogin(player));
		s.addLong(player.getUsernameHash());
		s.addLong(DataConversions.IPToLong(player.getCurrentIP()));
		s.addBytes(player.getPassword().getBytes());
		//s.addBytes(DataConversions.sha1(player.getPassword()).getBytes());
		connector.getSession().write(s.toPacket());
	}

	public static int PACKETS = 0;
	
	public void playerLogout(final long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(30);
		s.addLong(user);
		s.addLong(user);
		connector.getSession().write(s.toPacket());
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

			@Override
			public int[] getAssociatedIdentifiers() {
				return new int[]{1};
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
		connector.getSession().write(s.toPacket());
	}

	public void removeFriend(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(12);
		s.addLong(user);
		s.addLong(friend);
		connector.getSession().write(s.toPacket());
	}

	public void removeIgnore(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(14);
		s.addLong(user);
		s.addLong(friend);
		connector.getSession().write(s.toPacket());
	}

	public void reportUser(long user, long reported, byte reason) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(15);
		s.addLong(user);
		s.addLong(reported);
		s.addByte(reason);
		connector.getSession().write(s.toPacket());
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
					int lastMoved = (int) ((GameEngine.getAccurateTimestamp() - p.readLong()) / 1000);
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

			@Override
			public int[] getAssociatedIdentifiers() {
				return null;
			}
		});
		connector.getSession().write(s.toPacket());
	}

	public void saveProfiles(boolean finalize) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(9);
		s.addByte(finalize ? (byte) 1 : 0);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, Channel session)
					throws Exception {
				if (p.readByte() != 1) {
					Logger.error("Error saving all profiles!");
				}
			}

			@Override
			public int[] getAssociatedIdentifiers() {
				return new int[]{9};
			}
		});
		connector.getSession().write(s.toPacket());
	}

	public void sendPM(long user, long friend, boolean avoidBlock, byte[] message) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(10);
		s.addLong(user);
		s.addLong(friend);
		s.addByte((byte) (avoidBlock ? 1 : 0));
		s.addBytes(message);
		connector.getSession().write(s.toPacket());
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

			@Override
			public int[] getAssociatedIdentifiers() {
				return new int[]{2};
			}
		});
		connector.getSession().write(s.toPacket());
	}
}
