package org.darkquest.ls.net.monitor;

import org.jboss.netty.channel.Channel;

public interface ActionListener {

	public void onLogin(Channel channel);
	
	public void onLogout(Channel channel);
}
