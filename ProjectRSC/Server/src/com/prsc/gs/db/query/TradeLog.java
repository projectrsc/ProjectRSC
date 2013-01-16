package com.prsc.gs.db.query;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.prsc.config.Constants;
import com.prsc.gs.model.InvItem;

/**
 * TODO: Change log to builder design (ex: new TradeLog().build())
 * Logic should never be involved in preparing statements other than passing data to the database
 * @author openfrog
 *
 */


public final class TradeLog extends GameLog {

	private String player1, player2, playerOnesOffer, playerTwosOffer;
	private List<InvItem> player1Offer, player2Offer;
	
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
		statement.setString(3, playerOnesOffer);
		statement.setString(4, playerTwosOffer);
		statement.setLong(5, time);
		return statement;
	}

	@Override
	public GameLog build() {
		StringBuilder sb = new StringBuilder();
		
		for (InvItem i : player1Offer) {
			//System.out.println("Player 1 offered: " + i.getID());
			sb.append(i.getID()).append(":").append(i.getAmount()).append(",");
		}
		
		playerOnesOffer = sb.toString();
		sb = new StringBuilder();
		
		for (InvItem i : player2Offer) {
			//System.out.println("Player 2 offered: " + i.getID());
			sb.append(i.getID()).append(":").append(i.getAmount()).append(",");
		}
		
		playerTwosOffer = sb.toString();
		return this;
	}

}
