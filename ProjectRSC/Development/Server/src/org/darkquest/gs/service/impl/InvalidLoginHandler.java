package org.darkquest.gs.service.impl;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.darkquest.gs.service.Service;

/**
 * 
 * @author Hikilaka
 *
 * @version 1.0
 * 
 * This class handles when a connection is attempted
 * with invalid credentials, when invalid credentials
 * are given 5 times, all connection attempts are blocked
 * for 5 minutes after the last connection has been attempted.
 * 
 */
public final class InvalidLoginHandler extends Service {

	private final Map<String, Integer> connectionAttempts = new ConcurrentHashMap<>();

	private final Map<String, TimerTask> declineAttempts = new ConcurrentHashMap<>();

	private final Timer timer = new Timer();

	@Override
	public void start() {
		// nothing to do upon startup
	}

	public boolean acceptConnection(String ip) {
		return !declineAttempts.containsKey(ip);
	}

	public void incrementConnectionAttempt(final String ip) {
		Integer attempts = 1;

		if (connectionAttempts.containsKey(ip)) {
			attempts = connectionAttempts.get(ip) + 1;
		}

		connectionAttempts.put(ip, attempts);

		if (attempts >= 5) { // schedule the task to remove 5 minutes from now
			TimerTask task = declineAttempts.get(ip); // get the current timer task

			if (task != null) { // cancel the current task
				task.cancel();
				timer.purge(); // remove all canceled tasks
			}
			
			task = new TimerTask() { // add/restart a timer task
				@Override
				public void run() {
					connectionAttempts.remove(ip);
					declineAttempts.remove(ip);
				}
			};

			declineAttempts.put(ip, task);
			timer.schedule(task, 300000); // 5 minutes
		}
	}

}
