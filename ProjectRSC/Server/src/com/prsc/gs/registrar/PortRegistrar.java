package com.prsc.gs.registrar;

import java.util.HashMap;

import java.util.Map;

import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.registrar.impl.Captcha;
import com.prsc.gs.registrar.impl.PacketHandlers;


public final class PortRegistrar {
	
	private static final Map<Class<? extends Port>, Port> ports = new HashMap<Class<? extends Port>, Port>();
	
	static { // Initialize content 
		addPort(new PacketHandlers());		
		addPort(new Captcha());
		//registerPort(new DatabaseManager());
	}
	
	public static void register() throws Exception {		
		PluginHandler.getPluginHandler().initPluginRegisters();
		for (Port port  : ports.values()) {
			port.register();
		}
	}
	
	public static <T extends Port> void addPort(T t) {
		ports.put(t.getClass(), t);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Port> T lookup(Class<T> class_) {
		return (T) ports.get(class_);
	}
}
