package org.darkquest.gs.connection.filter;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.darkquest.config.Constants;
import org.darkquest.ls.util.DataConversions;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * PRSC Connection Filter v1.2- Acts as a DoS protection module by restricting incoming connections.
 * This class is added to Netty's pipeline before passing the connection to it's appropriate handler
 * @author openfrog
 * 
 * TODO: Add protection when multiple connections are opened at once
 *
 */

public final class ConnectionFilter extends SimpleChannelUpstreamHandler {
	
	private static ConnectionFilter filter = null;
	private ConcurrentHashMap<Long, Integer> clientConnections = null, invalidAttempts = null;
	private ConcurrentHashMap<Long, TimerTask> cancelQueue = null;
	private ArrayList<Long> currentBans = null;
	private volatile int maxAllowed = 0;
	
	private final Timer timer = new Timer();
	
	public static ConnectionFilter getInstance(int maxAllowed) {
		if(filter == null) {
			filter = new ConnectionFilter(maxAllowed);
		}
		return filter;
	}
	
	public static ConnectionFilter getInstance() {
		return filter;
	}
	
	private ConnectionFilter(int maxAllowed) {
		this.maxAllowed = maxAllowed;
		this.clientConnections = new ConcurrentHashMap<Long, Integer>(); // monitors the raw connection attempt as is
		this.invalidAttempts = new ConcurrentHashMap<Long, Integer>(); // monitors invalid attempts (or ones that werent intentional)
		this.cancelQueue = new ConcurrentHashMap<Long, TimerTask>(); // monitors scheduled cancellations
		this.currentBans = new ArrayList<Long>();
		this.clearBlacklist();
	}
	
	@Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		String ip = ((InetSocketAddress) ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress();
		final long encoded = DataConversions.IPToLong(ip);
		
		Integer count = getCurrentClients().get(encoded) == null ? 0 : getCurrentClients().get(encoded);
		
		if(count > maxAllowed && currentBans.contains(encoded)) { // should not happen
			ctx.getChannel().disconnect();
			return;
		} else if(count > maxAllowed && invalidAttempts.containsKey(encoded)) { // timeout and wait 5 minutes
			TimerTask currentCancelTask = cancelQueue.get(encoded);
			
			if(currentCancelTask != null) {
				currentCancelTask.cancel();
				timer.purge();
			} 
			currentCancelTask = new TimerTask() {

				@Override
				public void run() {
					//System.out.println("Removing");
					invalidAttempts.remove(encoded);
					clientConnections.remove(encoded);
					cancelQueue.remove(encoded);
				}
				
			};
			timer.schedule(currentCancelTask, 10000);
			cancelQueue.put(encoded, currentCancelTask);
			ctx.getChannel().disconnect();
			return;
		} else if(count > maxAllowed) { // send to blacklist
			currentBans.add(encoded);
			toBlacklist(ip, true);// add to blacklist
	        ctx.getChannel().disconnect();
			return;
		}
		else {
			count = incrementAndGet(encoded, false);
		}
		
		super.channelOpen(ctx, e);
	}
	
	public void processCleanLogin(Channel channel, boolean isInvalid) {
		long encoded = DataConversions.IPToLong(((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress());
		
		if(isInvalid) {
			incrementAndGet(encoded, true);
		} else { // just remove we can assume its a clean login
			decrementAndGet(encoded, false);
			
			if(getCurrentAttempts().containsKey(encoded)) // Remove any previous attempts
				getCurrentAttempts().remove(encoded);
		}
	}
	
	public Map<Long, Integer> getCurrentClients() {
		return clientConnections;
	}
	
	public Map<Long, Integer> getCurrentAttempts() {
		return invalidAttempts;
	}
	
	public ArrayList<Long> getCurrentBans() {
		return currentBans;
	}
	
	public void setTo(final long hash, final int count, final boolean isInvalid) {
		if(isInvalid)
			invalidAttempts.put(hash, count);
		else
			clientConnections.put(hash, count);
	}
	
	public void clearBlacklist() {
		try {
			File f = new File(Constants.GameServer.BAN_LOCATION);
			if(!f.exists()) {
				System.out.println("Error: No ban script found at " + Constants.GameServer.BAN_LOCATION);
				return;
			}
			System.out.println("Cleaned blacklist");
			Runtime.getRuntime().exec(Constants.GameServer.BAN_LOCATION + " unbanall"); //ban.sh with args <ban/unban/unbanall> <ip>
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void toBlacklist(String ip, boolean ban) {
		try {
			File f = new File(Constants.GameServer.BAN_LOCATION);
			if(!f.exists()) {
				System.out.println("Error: No ban script found at " + Constants.GameServer.BAN_LOCATION);
				return;
			}
			System.out.println("Adding " + ip + " to blacklist");
			Runtime.getRuntime().exec(Constants.GameServer.BAN_LOCATION + " " + (ban ? "ban " : "unban ") + ip); //ban.sh with args <ban/unban/unbanall> <ip>
			if(!ban)
				currentBans.remove(DataConversions.IPToLong(ip));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private int incrementAndGet(long hash, boolean isInvalid) {
		final int count;
		Integer i = isInvalid ? invalidAttempts.get(hash) : clientConnections.get(hash);
		
		if(i == null) {
			count = 1;
		} else {
			count = i + 1;
		}
		
		if(isInvalid)
			invalidAttempts.put(hash, count);
		else
			clientConnections.put(hash, count);
		return count;
	}
	
	private int decrementAndGet(long hash, boolean isInvalid) {
		final int count;
		Integer i = isInvalid ? invalidAttempts.get(hash) : clientConnections.get(hash);
		
		if(i == null) {
			count = 0;
		} else {
			count = i - 1;
		}
		
		if(isInvalid)
			invalidAttempts.put(hash, count);
		else
			clientConnections.put(hash, count);
		return count;
	}
}
