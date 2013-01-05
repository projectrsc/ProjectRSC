package com.prsc.gs.plugins.lang;

import java.io.File;


import java.util.List;

import com.prsc.gs.plugins.PlugInterface;
import com.prsc.gs.plugins.QuestInterface;
import com.prsc.gs.plugins.ShopInterface;
import com.prsc.gs.plugins.phandler.PacketHandler;

public interface GenericFactory {
	
	public List<PacketHandler> buildHandler(File handlerDir) throws Exception;
	public List<QuestInterface> buildQuests(File questsDir) throws Exception;
	public List<PlugInterface> buildPlugs(File npcDir) throws Exception;
	public List<ShopInterface> buildShops(File shopsDir);
}
