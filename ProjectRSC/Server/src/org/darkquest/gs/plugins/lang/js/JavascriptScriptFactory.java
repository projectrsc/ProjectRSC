package org.darkquest.gs.plugins.lang.js;

import java.io.File;
import java.util.List;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.plugins.NpcInterface;
import org.darkquest.gs.plugins.Quest;
import org.darkquest.gs.plugins.QuestInterface;
import org.darkquest.gs.plugins.ShopInterface;
import org.darkquest.gs.plugins.lang.GenericFactory;
import org.darkquest.gs.world.Shop;

public class JavascriptScriptFactory implements GenericFactory {

	@Override
	public List<QuestInterface> buildQuests(File jsQuestsDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ShopInterface> buildShops(File jsShopsDir) {
		// TODO Auto-generated method stub
		return null; 
	}

	@Override
	public List<NpcInterface> buildNpcs(File jsNpcsdir) {
		// TODO Auto-generated method stub
		return null;
	}

}
