package org.darkquest.ls.model;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.darkquest.ls.Server;
import org.darkquest.ls.packetbuilder.loginserver.MiscPacketBuilder;
import org.darkquest.ls.util.Config;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;

public final class World {

	private final TreeMap<Long, Integer> players = new TreeMap<Long, Integer>();

	private final TreeMap<Long, PlayerSave> saves = new TreeMap<Long, PlayerSave>();

	private final MiscPacketBuilder actionSender = new MiscPacketBuilder();

	private int id;

	private boolean members;

	private String location;

	private Channel channel;

	public World(int id, boolean members, String location, Channel session) {
		this.id = id;
		this.members = members;
		this.location = location;
		this.channel = session;
	}

	public void clearPlayers() {
		for (Entry<Long, Integer> player : getPlayers()) {
			long user = player.getKey();
			for (World w : Server.getServer().getWorlds()) {
				w.actionSender.friendLogout(user);
			}
		}
		players.clear();
	}

	public boolean hasPlayer(long user) {
		boolean contains = players.containsKey(user);

		if (contains) {
			if (players.get(user) == null) {
				return false;
			}
		}
		return contains;
	}

	public void assosiateSave(PlayerSave save) {
		saves.put(save.getUser(), save);
	}

	public void unassosiateSave(PlayerSave save) {
		saves.remove(save.getUser());
	}

	public PlayerSave getSave(long user) {
		return saves.get(user);
	}

	public void registerPlayer(long user, String ip) {
		Server server = Server.getServer();
		ResultSet result;
		try {
			result = Server.db.getQuery("SELECT owner, block_private FROM `" + Config.MYSQL_TABLE_PREFIX + "players` WHERE `user`='" + user + "'");
			if (!result.next()) {
				return;
			}
			int owner = result.getInt("owner");
			boolean blockPrivate = result.getInt("block_private") == 1;

			result = Server.db.getQuery("SELECT user FROM `" + Config.MYSQL_TABLE_PREFIX + "friends` WHERE `friend`='" + user + "'" + (blockPrivate ? " AND user IN (SELECT friend FROM `" + Config.MYSQL_TABLE_PREFIX + "friends` WHERE `user`='" + user + "')" : ""));
			while (result.next()) {
				long friend = result.getLong("user");
				World w = server.findWorld(friend);
				if (w != null) {
					w.getActionSender().friendLogin(friend, user, id);
				}
			}
			long now = (int) (System.currentTimeMillis() / 1000);
			Server.db.updateQuery("INSERT INTO `" + Config.MYSQL_TABLE_PREFIX + "logins`(`user`, `time`, `ip`) VALUES('" + user + "', '" + now + "', '" + ip + "')");
			Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET login_date=" + now + ", login_ip='" + ip + "' WHERE user='" + user + "'");

			players.put(user, owner);
			System.out.println("Added " + DataConversions.hashToUsername(user) + " to world " + id);
		} catch (Exception e) {
			Server.error(e);
		}
	}

	public void unregisterPlayer(long user) {
		
			players.remove(user);
			try {
				for (World w : Server.getServer().getWorlds()) {
					w.getActionSender().friendLogout(user);
				}
				System.out.println("LS: Starting removal of " + DataConversions.hashToUsername(user) + " from world " + id);
				if(players.containsKey(user)) {
					System.out.println("LS: FAILED TO REMOVE " + DataConversions.hashToUsername(user) + " from the WORLD COLLECTION on world " + id);
				} else {
					System.out.println("LS: Successfully removed " + DataConversions.hashToUsername(user) + " from the WORLD COLLECTION on world " + id);
				}


				Server.db.updateQuery("UPDATE `" + Config.MYSQL_TABLE_PREFIX + "players` SET online=0 WHERE user='" + user + "'");
			} catch (Exception e) {
				Server.error(e);
			} finally {
				System.out.println("LS: Successfully removed " + DataConversions.hashToUsername(user) + " from world " + id);
			}
		
	}

	public int getID() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isMembers() {
		return members;
	}

	public void setMembers(boolean members) {
		this.members = members;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	public Collection<Entry<Long, PlayerSave>> getAssosiatedSaves() {
		return saves.entrySet();
	}

	public Collection<Entry<Long, Integer>> getPlayers() {
		return players.entrySet();
	}

}
