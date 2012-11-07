package org.darkquest.gs.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.darkquest.config.Constants;

public final class ChatLog extends DatabaseLog {

	private final String sender, message;

	private final ArrayList<String> revievers;

	public ChatLog(String sender, String message) {
		this(sender, message, null);
	}

	public ChatLog(String sender, String message, ArrayList<String> recievers) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "chat_logs`(`sender`, `message`, `recievers`, `time`) VALUES(?, ?, ?, ?)");
		this.sender = sender;
		this.message = message;
		this.revievers = recievers;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, sender);
		statement.setString(2, message);

		StringBuilder sb = new StringBuilder();

		if (revievers != null) {
			for (String user : revievers) {
				sb.append(user).append(",");
			}
		}
		statement.setString(3, revievers == null ? "no one" : sb.toString());
		statement.setLong(4, time);
		return statement;
	}

}
