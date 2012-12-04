package com.prsc.gs.db.query;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;

public abstract class GameLog {
	
	protected final String query;
		
	protected final long time;
	
	public GameLog(String query) {
		this.query = query;
		this.time = System.currentTimeMillis() / 1000;
	}
	
	public abstract GameLog build(); // add any logic here
	
	public abstract PreparedStatement prepareStatement(Connection connection) throws SQLException;
	
}
