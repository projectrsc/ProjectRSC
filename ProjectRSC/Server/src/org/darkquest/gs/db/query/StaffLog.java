package org.darkquest.gs.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.darkquest.config.Constants;
import org.darkquest.gs.model.Player;

/**
 * staff username|action int|affected player username|time in epoch|staff x|staff y|affected x|affected y|staff ip|affected ip
 * @author openfrog
 *
 */

public final class StaffLog extends GameLog {
	
	private Player staffMember, affectedPlayer;
	private String staffUsername, affectedUsername, staffIp, affectedIp;
	private int action, staffX, staffY, affectedX, affectedY;

	public StaffLog(Player staffMember, int action, Player affectedPlayer) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX 
		+ "staff_logs`(`staff_username`, `action`, `affected_player`, `time`, `staff_x`, `staff_y`, `affected_x`, `affected_y`, `staff_ip`, `affected_ip`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		this.staffMember = staffMember;
		this.action = action;
		this.affectedPlayer = affectedPlayer;
	}

	@Override
	public PreparedStatement prepareStatement(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, staffUsername);
		statement.setInt(2, action);
		statement.setString(3, affectedUsername);
		statement.setLong(4, time);
		statement.setInt(5, staffX);
		statement.setInt(6, staffY);
		statement.setInt(7, affectedX);
		statement.setInt(8, affectedY);
		statement.setString(9, staffIp);
		statement.setString(10, affectedIp);
		return statement;
	}

	@Override
	public GameLog build() {
		this.staffUsername = staffMember.getUsername();
		this.affectedUsername = affectedPlayer.getUsername();
		this.staffX = staffMember.getX();
		this.staffY = staffMember.getY();
		this.affectedX = affectedPlayer.getX();
		this.affectedY = affectedPlayer.getY();
		this.staffIp = staffMember.getCurrentIP();
		this.affectedIp = affectedPlayer.getCurrentIP();
		return this;
	}

}
