package org.darkquest.gs.connection.filter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * PRSC Connection Filter v1.1- Acts as a DoS protection module by restricting incoming connections.
 * This class is added to Netty's pipeline before passing the connection to it's appropriate handler
 * @author openfrog
 * 
 * TODO: Add protection when multiple connections are opened at once
 *
 */

public final class ConnectionFilter extends SimpleChannelUpstreamHandler {
	
	private static ConnectionFilter filter = null;
	private ConcurrentHashMap<Long, Integer> clientConnections = null; 
	private volatile int maxAllowed = 0;
	
	public static ConnectionFilter getInstance(int maxAllowed) {
		if(filter == null) {
			filter = new ConnectionFilter(maxAllowed);
		}
		return filter;
	}
	
	private ConnectionFilter(int maxAllowed) {
		this.maxAllowed = maxAllowed;
		this.clientConnections = new ConcurrentHashMap<Long, Integer>(); 
	}
	
	public Map<Long, Integer> getCurrentClients() {
		return clientConnections;
	}
	
	public boolean isMaxed(long hash) {
		return clientConnections.containsKey(hash) ? (clientConnections.get(hash) + 1) > maxAllowed : false;
	}
	
	public int incrementAndGet(long hash) {
		final int count;
		
		Integer i = clientConnections.get(hash);
		if(i == null) {
			count = 1;
		} else {
			count = i + 1;
		}
		clientConnections.put(hash, count);
		return count;
	}
	
	public int decrementAndGet(long hash) {
		final int count;
		
		Integer i = clientConnections.get(hash);
		if(i == null) {
			count = 0;
		} else {
			count = i - 1;
		}
		clientConnections.put(hash, count);
		return count;
	}
}
