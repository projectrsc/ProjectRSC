package com.prsc.gs.service;

import java.util.HashMap;
import java.util.Map;

import com.prsc.gs.db.DatabaseManager;
import com.prsc.gs.service.impl.Captcha;
import com.prsc.gs.service.impl.PacketHandlers;

/**
 * @author Hikilaka
 * @version 1
 */
public final class Services {
	
	private static final Map<Class<? extends Service>, Service> services = new HashMap<>();
	
	public static void init() {
		/**
		 * add services here!
		 */
		registerService(new PacketHandlers());		
		registerService(new Captcha());
		registerService(new DatabaseManager());
	}
	
	public static void start() {
		for (Service service  : services.values()) {
			service.start();
		}
	}
	
	public static <T extends Service> void registerService(T t) {
		services.put(t.getClass(), t);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Service> T lookup(Class<T> class_) {
		return (T) services.get(class_);
	}

}
