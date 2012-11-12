package org.darkquest.gs.plugins.lang;

import java.io.File;


import java.util.List;

import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PlugInterface;
import org.darkquest.gs.plugins.QuestInterface;
import org.darkquest.gs.plugins.ShopInterface;

public interface GenericFactory {
	
	public List<PacketHandler> buildHandler(File handlerDir) throws Exception;
	public List<QuestInterface> buildQuests(File questsDir) throws Exception;
	public List<PlugInterface> buildPlugs(File npcDir) throws Exception;
	public List<ShopInterface> buildShops(File shopsDir);
}
