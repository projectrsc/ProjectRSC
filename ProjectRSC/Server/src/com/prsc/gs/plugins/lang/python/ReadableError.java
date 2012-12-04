package com.prsc.gs.plugins.lang.python;

public class ReadableError {
	
	public static String[] toReadable(String error) {
		String[] order = new String[4];
		try {
			String[] spl = error.split(",");
			order[0] = "@yel@Description: @cya@(@whi@" + (spl[0].contains("(") ? spl[0].replace("(", "") : spl[0]) + "@cya@) ";
			order[1] = "@yel@Syntax: @cya@(@whi@" + spl[4] + "@cya@) ";
			order[2] = "@yel@Line: @cya@(@whi@" + spl[2].trim() + "@cya@) ";
			String fileName = spl[1].substring(spl[1].lastIndexOf("/") + 1, spl[1].indexOf(".py") + 3) + " ";
			order[3] = "@yel@File: @cya@(@whi@" + fileName.trim() + "@cya@)";
		} catch(Exception e) {
			e.printStackTrace();
			order[0] = "@whi@Unable to parse out message";
		}
		return order;
	}

}
