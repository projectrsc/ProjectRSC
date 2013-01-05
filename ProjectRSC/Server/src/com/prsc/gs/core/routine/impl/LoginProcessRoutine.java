package com.prsc.gs.core.routine.impl;

import java.util.ArrayList;
import java.util.List;

import org.python.google.common.collect.Lists;

import com.prsc.gs.Server;
import com.prsc.gs.connection.LSPacket;
import com.prsc.gs.core.LoginConnector;
import com.prsc.gs.core.TaskManager;
import com.prsc.gs.core.routine.Routine;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.registrar.PortRegistrar;
import com.prsc.gs.registrar.impl.PacketHandlers;
import com.prsc.gs.util.Logger;

public class LoginProcessRoutine extends Routine {
	
	private List<LSPacket> packetQueue = null;
	private int numWorkers = TaskManager.WORKER_COUNT;

	public LoginProcessRoutine(List<LSPacket> packetQueue) {
		this.packetQueue = packetQueue;
	}
	
	private void computeNow() {
		for(LSPacket p : packetQueue) {
			PacketHandler handler = null;
			if (((handler = Server.getInstance().getLoginConnector().getUniqueHandlers().get(p.getUID())) != null) 
					|| ((handler = PortRegistrar.lookup(PacketHandlers.class).getLoginHandlers().get(p.getID())) != null)) {
				try {
					handler.handlePacket(p, Server.getInstance().getLoginConnector().getSession());
					Server.getInstance().getLoginConnector().getUniqueHandlers().remove(p.getUID());
				} catch (Exception e) {
					Logger.error("Exception with p[" + p.getID() + "] from LOGIN_SERVER: " + e.getMessage());
				}
			} else {
				Logger.error("Unhandled packet from LS: " + p.getID());
			}
		}
	}
	
	@Override
	protected void compute() {
		int maxWorkers = numWorkers * 16;
		
		if(packetQueue.isEmpty()) {
			return;
		}
		
		if(packetQueue.size() < maxWorkers) {
			computeNow();
			return;
		}
		
		int split = packetQueue.size() / numWorkers;
		
		List<LoginProcessRoutine> tasks = new ArrayList<LoginProcessRoutine>();
		List<List<LSPacket>> partitioned = Lists.partition(packetQueue, split);
		for(List<LSPacket> sub : partitioned) {
			tasks.add(new LoginProcessRoutine(sub));
		}
		invokeAll(tasks);
 	}

}
