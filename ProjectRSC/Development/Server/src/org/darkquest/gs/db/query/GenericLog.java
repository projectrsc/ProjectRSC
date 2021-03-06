package org.darkquest.gs.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.darkquest.config.Constants;

public final class GenericLog extends DatabaseLog {
	
	private final String message;
	
	public GenericLog(String message) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "generic_logs`(`message`, `time`) VALUES(?, ?)");
		this.message = message;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, message);
		statement.setLong(2, time);
		return statement;
	}
	
}
