package org.darkquest.gs.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.InvItem;

public final class TradeLog extends DatabaseLog {

	private final String player1, player2;

	private final List<InvItem> player1Offer, player2Offer;

	public TradeLog(String player1, String player2, List<InvItem> player1Offer, List<InvItem> player2Offer) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "trade_logs`(`player1`, `player2`, `player1_items`, `player2_items`, `time`) VALUES(?, ?, ?, ?, ?)");
		this.player1 = player1;
		this.player2 = player2;
		this.player1Offer = player1Offer;
		this.player2Offer = player2Offer;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, player1);
		statement.setString(2, player2);

		StringBuilder sb = new StringBuilder();

		for (InvItem i : player1Offer) {
			sb.append(i.getID()).append(":").append(i.getAmount()).append(",");
		}
		statement.setString(3, sb.toString());

		sb = new StringBuilder();

		for (InvItem i : player2Offer) {
			sb.append(i.getID()).append(":").append(i.getAmount()).append(",");
		}
		statement.setString(4, sb.toString());
		statement.setLong(5, time);
		return statement;
	}

}
