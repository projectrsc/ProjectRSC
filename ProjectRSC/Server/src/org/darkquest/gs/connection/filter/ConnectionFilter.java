package org.darkquest.gs.connection.filter;

import java.net.InetSocketAddress;


import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.darkquest.gs.connection.RSCPacket;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * PRSC Connection Filter - Acts as a DoS protection module by restricting incoming connections.
 * This class is added to Netty's pipeline before passing the connection to it's appropriate handler
 * @author openfrog
 *
 */

public final class ConnectionFilter extends SimpleChannelUpstreamHandler {
	
	private static ConnectionFilter filter = null;
	private Map<Long, Integer> clientConnections = null; 
	private Map<Long, TimerTask> resetQueue = null;
	private volatile int maxAllowed = 0;
	
	private final Timer connectionReset = new Timer();
	
	public static ConnectionFilter getInstance(int maxAllowed) {
		if(filter == null) {
			filter = new ConnectionFilter(maxAllowed);
		}
		return filter;
	}
	
	private ConnectionFilter(int maxAllowed) {
		this.maxAllowed = maxAllowed;
		this.clientConnections = new ConcurrentHashMap<Long, Integer>(); 
		this.resetQueue = new ConcurrentHashMap<Long, TimerTask>();
	}
	
	public Map<Long, Integer> getCurrentClients() {
		return clientConnections;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {		
		RSCPacket p = (RSCPacket) e.getMessage();
		
		if(p.getID() == 39) { // check to see if they log out
			String remoteIp = ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress();
			final long encoded = DataConversions.IPToLong(remoteIp);
			decrementAndGet(encoded); 
		} 
		super.messageReceived(ctx, e);
	} 
	
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception  {
		if(maxAllowed > 0) {
			String remoteIp = ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress();
			
			final long encoded = DataConversions.IPToLong(remoteIp);
			int count = incrementAndGet(encoded);
			
			if(count > maxAllowed) {
				synchronized(resetQueue) {
					TimerTask currentTask = resetQueue.get(encoded);
				
					if(currentTask != null) {
						currentTask.cancel();
						connectionReset.purge();
					}
					currentTask = new TimerTask() {

						@Override
						public void run() {
							int count = decrementAndGet(encoded);
							if(count > maxAllowed) {
								setCount(encoded, 0);
							} 
							resetQueue.remove(encoded);
						}
					
					};
					resetQueue.put(encoded, currentTask);
					connectionReset.schedule(currentTask, 30000); 
				}
				ctx.getChannel().disconnect();
			}
	    }
		super.channelOpen(ctx, e);
	}
	
	private int incrementAndGet(long hash) {
		final int count;
		
		synchronized (clientConnections) {
			Integer i = clientConnections.get(hash);
			if(i == null) {
				count = 1;
			} else {
				count = i + 1;
			}
			clientConnections.put(hash, count);
		}
		return count;
	}
	
	private int decrementAndGet(long hash) {
		final int count;
		
		synchronized (clientConnections) {
			Integer i = clientConnections.get(hash);
			if(i == null) {
				count = 1;
			} else {
				count = i - 1;
			}
			clientConnections.put(hash, count);
		}
		return count;
	}
	
	private void setCount(long hash, int nCount) {
		final int count;
		
		synchronized (clientConnections) {
			Integer i = clientConnections.get(hash);
			if(i == null) {
				count = 1;
			} else {
				count = nCount;
			}
			clientConnections.put(hash, count);
		}
	}
}
