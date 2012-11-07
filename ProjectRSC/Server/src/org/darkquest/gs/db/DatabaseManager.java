package org.darkquest.gs.db;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.darkquest.config.Constants;
import org.darkquest.gs.db.query.DatabaseLog;
import org.darkquest.gs.service.Service;
import org.darkquest.gs.util.Logger;

public final class DatabaseManager extends Service implements Runnable {

	private final DBConnection connection = new DBConnection();

	private final BlockingQueue<DatabaseLog> queries = new ArrayBlockingQueue<DatabaseLog>(5000);

	private final AtomicBoolean running = new AtomicBoolean(true);
	
	private final Thread thread = new Thread(this, "Database logging thread");
	
	private final Object lock = new Object();

	public DatabaseManager() {
		connection.createConnection(Constants.GameServer.MYSQL_DB);
		if (!connection.isConnected()) {
			System.out.println("Database Logging thread not connected to MySQL");
		}
	}

	public synchronized void start() {
		running.set(true);
		thread.start();
	}

	public synchronized void shutdown() {
		running.set(false);
	}

	@Override
	public synchronized void run() {
		while (running.get() && connection.isConnected()) {
			if (queries.size() > 0) {
				try {
					DatabaseLog log = queries.poll();

					if (log != null) {
						log.prepareStatement(connection.getConnection()).execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				while (queries.size() <= 0) {
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		Logger.println("Shutting down database thread.. executing remaining queries");
		while (queries.size() < 1 && connection.isConnected()) {
			try {
				DatabaseLog log = queries.poll();

				if (log != null) {
					log.prepareStatement(connection.getConnection()).execute();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addQuery(DatabaseLog log) {
		if (!running.get()) {
			return;
		}

		queries.add(log);

		synchronized (lock) {
			lock.notifyAll();
		}
	}

}