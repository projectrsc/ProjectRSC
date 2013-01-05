package com.prsc.gs.core.routine.impl;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;

import org.python.google.common.collect.Lists;

import com.prsc.gs.connection.Client;
import com.prsc.gs.core.TaskManager;
import com.prsc.gs.core.routine.Routine;

public class ClientProcessingRoutine extends Routine {
	
	private static final long serialVersionUID = 1L;

	private List<Client> clients = null;
	
	private int numWorkers = TaskManager.WORKER_COUNT;
	
	public ClientProcessingRoutine(List<Client> clients) {
		this.clients = clients;
	}
	
	private void executeNow() {
		for(Client client : clients) {
			client.process(); // process packets
		}
	}

	@Override
	protected void compute() {
		int maxWorkers = numWorkers * 16; 
	
		if(clients.isEmpty()) {
			return;
		}
		
		if(clients.size() < maxWorkers) {
			executeNow();
			return;
		}
		
		int split = clients.size() / numWorkers;
		
		List<ClientProcessingRoutine> tasks = new ArrayList<ClientProcessingRoutine>();
		List<List<Client>> clientPartitions = Lists.partition(clients, split);
		
		for(List<Client> partition : clientPartitions) {
			tasks.add(new ClientProcessingRoutine(partition));
		}
		invokeAll(tasks);
	}

}
