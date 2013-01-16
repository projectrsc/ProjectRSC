package com.prsc.gs.plugins.task.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.RecursiveAction;

import com.prsc.gs.core.TaskManager;
import com.prsc.gs.plugins.task.Task;

public class PluginExecutionTask extends Task {
	
	private Method method = null;
	private Object obj = null;
	private Object[] data = null;
	
	public PluginExecutionTask(Method method, Object obj, Object[] data) {
		this.method = method;
		this.obj = obj;
		this.data = data;
	}
	
	@Override
	public void run() {
		try {
			method.invoke(obj, data);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
