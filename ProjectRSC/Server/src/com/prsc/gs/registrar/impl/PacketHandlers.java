package com.prsc.gs.registrar.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.prsc.config.Constants;
import com.prsc.gs.plugins.PluginHandler;
import com.prsc.gs.plugins.phandler.PacketHandler;
import com.prsc.gs.registrar.Port;

public final class PacketHandlers extends Port {
	
	private final Map<Integer, PacketHandler> gameHandlers = new ConcurrentHashMap<Integer, PacketHandler>();

	private final Map<Integer, PacketHandler> loginHandlers = new ConcurrentHashMap<Integer, PacketHandler>();
	
	@Override
	public void register() throws Exception {
		List<Class<?>> classList = PluginHandler.loadClasses("com.prsc.gs.plugins.phandler.client");
		for(Class<?> clazz : classList) {
			if(!clazz.getName().contains("$")) { // ignore inner classes
				Object handlerInstance = (Object) clazz.newInstance();
				if(handlerInstance instanceof PacketHandler) {
					PacketHandler handler = (PacketHandler) handlerInstance;
					registerGameHandler(handler, handler.getAssociatedIdentifiers());
				} 
			}
		}
		getPythonGameHandlers();
		classList = PluginHandler.loadClasses("com.prsc.gs.plugins.phandler.ls");
		for(Class<?> clazz : classList) {
			if(!clazz.getName().contains("$")) { // ignore inner classes
				Object handlerInstance = (Object) clazz.newInstance();
				if(handlerInstance instanceof PacketHandler) {
					PacketHandler handler = (PacketHandler) handlerInstance;
					registerLoginHandler(handler, handler.getAssociatedIdentifiers());
				}
			}
		} 
		classList.clear();
		/*
		registerGameHandler(new Ping(), 5);
		registerGameHandler(new ReportHandler(), 13);
		registerGameHandler(new InvUseOnPlayer(), 16);
		registerGameHandler(new InvUseOnItem(), 27);
		registerGameHandler(new SessionRequest(), 32);
		registerGameHandler(new InvUseOnGroundItem(), 34);
		registerGameHandler(new DuelHandler(), 35, 123, 87, 222, 225, 252);
		registerGameHandler(new InvUseOnObject(), 36, 94);
		registerGameHandler(new PlayerLogout(), 39);
		registerGameHandler(new ObjectAction(), 40, 51);
		registerGameHandler(new StyleHandler(), 42);
		registerGameHandler(new BankHandler(), 48, 183, 198);
		registerGameHandler(new PrayerHandler(), 56, 248);
		registerGameHandler(new AttackHandler(), 57, 73);
		registerGameHandler(new FollowRequest(), 68);
		registerGameHandler(new NpcCommand(), 74);
		registerGameHandler(new PlayerLogin(), 77);
		registerGameHandler(new PlayerAppearanceIDHandler(), 83);
		registerGameHandler(new InvActionHandler(), 89);
		registerGameHandler(new CommandHandler(), 90);
		registerGameHandler(new PlayerLogoutRequest(), 129);
		registerGameHandler(new WalkRequest(), 132);
		registerGameHandler(new InvUseOnNpc(), 142);
		registerGameHandler(new ChatHandler(), 145);
		registerGameHandler(new DropHandler(), 147);
		registerGameHandler(new MenuReplyHandler(), 154);
		registerGameHandler(new ExceptionHandler(), 156);
		registerGameHandler(new GameSettingHandler(), 157);
		registerGameHandler(new TradeHandler(), 166, 211, 216, 70, 53);
		registerGameHandler(new FriendHandler(), 168, 52, 25, 108, 254);
		registerGameHandler(new PrivacySettingHandler(), 176);
		registerGameHandler(new TalkToNpcHandler(), 177);
		registerGameHandler(new WieldHandler(), 181, 92);
		registerGameHandler(new SleepwordHandler(), 200);
		registerGameHandler(new SmithingHandler(), 201, 202);
		registerGameHandler(new SpellHandler(), 206, 55, 71, 49, 67, 17, 104, 232);
		registerGameHandler(new PlayerAppearanceUpdater(), 218);
		registerGameHandler(new WallObjectAction(), 235, 126);
		registerGameHandler(new PickupItem(), 245);
		registerGameHandler(new ShopHandler(), 253, 128, 255); */
	}
	
	public void getPythonGameHandlers() throws Exception {
		File pyHandlersDir = new File(Constants.GameServer.SCRIPTS_DIR +  "/python/phandlers/");
        if(!pyHandlersDir.exists()) {
            try {
                throw new FileNotFoundException("Python packet handlers directory not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(pyHandlersDir.listFiles().length > 0) { 
        	List<PacketHandler> handlers = PluginHandler.getPluginHandler().getPythonScriptFactory().buildHandler(pyHandlersDir);
        	for(PacketHandler handler : handlers) {
        		registerGameHandler(handler, handler.getAssociatedIdentifiers());
        	}
        }
	}
	
	public void registerGameHandler(PacketHandler handler, int... ids) {
		for (int i : ids) {
			gameHandlers.put(i, handler);
		}
	}
	
	public void unregisterGameHandler(PacketHandler handler) {
		if(gameHandlers.containsValue(handler)) {
			gameHandlers.remove(handler.getAssociatedIdentifiers());
		}
	}
	
	public void registerLoginHandler(PacketHandler handler, int... ids) {
		for (int i : ids) {
			loginHandlers.put(i, handler);
		}
	} 
	
	public Map<Integer, PacketHandler> getGameHandlers() {
		return gameHandlers;
	}
	
	public Map<Integer, PacketHandler> getLoginHandlers() {
		return loginHandlers;
	} 
}
