package com.prsc.gs.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.prsc.config.Constants;

public final class GameReport extends GameLog {

	private final String reporter, reported;

	private final byte reason;

	private final boolean mute;

	public GameReport(String reporter, String reported, byte reason, boolean mute) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "game_reports`(`time`, `reporter`, `reported`, `reason`, `muted`) VALUES(?, ?, ?, ?, ?)");
		this.reporter = reporter;
		this.reported = reported;
		this.reason = reason;
		this.mute = mute;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setLong(1, time);
		statement.setString(2, reporter);
		statement.setString(3, reported);
		statement.setByte(4, reason);
		statement.setBoolean(5, mute);
		return statement;
	}

	@Override
	public GameLog build() {
		return this;
	}

}
