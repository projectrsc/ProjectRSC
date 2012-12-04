package com.prsc.gs.plugins.lang.js;

import java.io.File;

import java.util.List;

import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.plugins.PlugInterface;
import com.prsc.gs.plugins.QuestInterface;
import com.prsc.gs.plugins.ShopInterface;
import com.prsc.gs.plugins.lang.GenericFactory;

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
