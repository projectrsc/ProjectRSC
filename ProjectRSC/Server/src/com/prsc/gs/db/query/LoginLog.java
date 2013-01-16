package com.prsc.gs.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.prsc.config.Constants;

public final class LoginLog extends GameLog {

	private final String player, ip;

	public LoginLog(String player, String ip) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "login_logs`(`player`, `ip`, `time`) VALUES(?, ?, ?)");
		this.player = player;
		this.ip = ip;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, player);
		statement.setString(2, ip);
		statement.setLong(3, time);
		return statement;
	}

	@Override
	public GameLog build() {
		return this;
	}

}
