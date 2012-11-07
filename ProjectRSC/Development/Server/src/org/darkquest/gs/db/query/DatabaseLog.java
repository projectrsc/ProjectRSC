package org.darkquest.gs.db.query;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;

public abstract class DatabaseLog {
	
	protected final String query;
		
	protected final long time;
	
	public DatabaseLog(String query) {
		this.query = query;
		this.time = System.currentTimeMillis() / 1000;
	}
	
	public abstract PreparedStatement prepareStatement(Connection connection) throws SQLException;
	
}
