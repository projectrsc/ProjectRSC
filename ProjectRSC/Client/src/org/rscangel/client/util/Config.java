package org.rscangel.client.util;

import java.io.File;

public final class Config {

	public static String SERVER_IP, CONF_DIR;
	
	public static int SERVER_PORT;
		
	public static long START_TIME;
	
	public static boolean BIG_CLIENT = false, MEMBERS_FEATURES = false;
	
	public static final int CLIENT_VERSION = 21; 
	
	public static void initConfig() {
		START_TIME = System.currentTimeMillis();
		
		if (System.getProperty("localhost", "false").equalsIgnoreCase("true")) {
			SERVER_IP = "localhost";
			CONF_DIR = "../data";
		} else {
			SERVER_IP = "208.64.121.156";
			CONF_DIR = System.getProperty("user.home") + File.separator + ".prsc";
		}
		SERVER_PORT = 43594;
		BIG_CLIENT = Boolean.parseBoolean(System.getProperty("bigclient", "false"));
		MEMBERS_FEATURES = Boolean.parseBoolean(System.getProperty("members", "false"));
	}
	
}
