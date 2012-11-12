package org.darkquest.gs.plugins.lang.js;

import java.io.File;

import java.util.List;

import org.darkquest.gs.phandler.PacketHandler;
import org.darkquest.gs.plugins.PlugInterface;
import org.darkquest.gs.plugins.QuestInterface;
import org.darkquest.gs.plugins.ShopInterface;
import org.darkquest.gs.plugins.lang.GenericFactory;

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
	public List<PlugInterface> buildPlugs(File jsNpcsdir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PacketHandler> buildHandler(File handlerDir) {
		// TODO Auto-generated method stub
		return null;
	}

}
