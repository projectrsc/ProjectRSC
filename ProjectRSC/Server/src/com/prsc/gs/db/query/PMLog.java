package com.prsc.gs.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.prsc.config.Constants;

public final class PMLog extends GameLog {

	private final String sender, message, reciever;
	
	public PMLog(String sender, String message, String reciever) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "private_message_logs`(`sender`, `message`, `reciever`, `time`) VALUES(?, ?, ?, ?)");
		this.sender = sender;
		this.message = message;
		this.reciever = reciever;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, sender);
		statement.setString(2, message);
		statement.setString(3, reciever);
		statement.setLong(4, time);
		return statement;
	}

	@Override
	public GameLog build() {
		return this;
	}

}
