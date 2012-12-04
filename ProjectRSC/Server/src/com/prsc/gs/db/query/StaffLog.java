package com.prsc.gs.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.prsc.config.Constants;
import com.prsc.gs.model.Player;

/**
 * staff username|action int|affected player username|time in epoch|staff x|staff y|affected x|affected y|staff ip|affected ip
 * @author openfrog
 *
 * 0 - Mute
 * 1 - Unmuted
 * 2 - Summon
 * 3 - Goto
 * 4 - Take
 * 5 - Put
 * 6 - kick
 * 7 - update
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

	public StaffLog(Player staffMember, int action) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX 
		+ "staff_logs`(`staff_username`, `action`, `affected_player`, `time`, `staff_x`, `staff_y`, `affected_x`, `affected_y`, `staff_ip`, `affected_ip`, `extra`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		this.staffMember = staffMember;
		this.action = action;
	}
	
	public StaffLog(Player staffMember, int action, String affectedUsername) {
		super("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX 
		+ "staff_logs`(`staff_username`, `action`, `affected_player`, `time`, `staff_x`, `staff_y`, `affected_x`, `affected_y`, `staff_ip`, `affected_ip`, `extra`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		this.staffMember = staffMember;
		this.action = action;
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
		this.staffUsername = staffMember == null ? "" : staffMember.getUsername();
		this.affectedUsername = affectedPlayer == null ? "" : affectedPlayer.getUsername();
		this.staffX = staffMember == null ? 0 : staffMember.getX();
		this.staffY = staffMember == null ? 0 :staffMember.getY();
		this.affectedX = affectedPlayer == null ? 0 : affectedPlayer.getX();
		this.affectedY = affectedPlayer == null ? 0 : affectedPlayer.getY();
		this.staffIp = staffMember == null ? "" :staffMember.getCurrentIP();
		this.affectedIp = affectedPlayer == null ? "" : affectedPlayer.getCurrentIP();
		return this;
	}

}
