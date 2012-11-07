package org.darkquest.gs.plugins.lang;

import java.io.File;
import java.util.List;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.plugins.NpcInterface;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.QuestInterface;
import org.darkquest.gs.plugins.ShopInterface;
import org.darkquest.gs.world.Shop;

public interface GenericFactory {
	
	public List<QuestInterface> buildQuests(File questsDir) throws Exception;
	public List<NpcInterface> buildNpcs(File npcDir) throws Exception;
	public List<ShopInterface> buildShops(File shopsDir);
}
