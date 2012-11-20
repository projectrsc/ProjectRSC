package org.darkquest.ls.net.monitor;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;

/**
 * Monitors any imposed rules against accounts
 * @author openfrog
 *
 */

public final class Monitor implements ActionListener {
	
	private static Monitor monitor;
	
	private ConcurrentHashMap<Long, Integer> successfulAttempts = null;
	
	private int ipPerAccount = 2; // We'll allow this by default and add in a command to change this
	
	public static Monitor getInstance() {
		if(monitor == null) {
			monitor = new Monitor();
		}
		return monitor;
	}
	
	private Monitor() {
		this.successfulAttempts = new ConcurrentHashMap<Long, Integer>(); 
	}
	
	public boolean hasMaxedLimit(long hash) {
		return successfulAttempts.containsKey(hash) ? (successfulAttempts.get(hash) + 1) > ipPerAccount : false;
	}
	
	@Override
	public void onLogin(Channel channel) {
		long encoded = DataConversions.IPToLong(((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress());
		System.out.println("PROCESS LOGIN");
		successIncrement(encoded);
	}

	@Override
	public void onLogout(Channel channel) {
		long encoded = DataConversions.IPToLong(((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress());
		System.out.println("PROCESS LOGOUT");
		successDecrement(encoded);
	}
	
	public void successIncrement(final long hash) {
		final int count;
		
		Integer i = successfulAttempts.get(hash);
		
		if(i == null) {
			count = 1;
		} else {
			count = i + 1;
		}
		System.out.println("current success: " + count);
		successfulAttempts.put(hash, count);
	}
	
	public void successDecrement(final long hash) {
		final int count;
		
		Integer i = successfulAttempts.get(hash);
		
		if(i == null) {
			count = 0;
		} else {
			count = i - 1;
		}
		
		successfulAttempts.put(hash, count);
	}
}
