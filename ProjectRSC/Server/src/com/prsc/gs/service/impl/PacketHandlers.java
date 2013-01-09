package com.prsc.gs.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.prsc.gs.phandler.PacketHandler;
import com.prsc.gs.phandler.client.AttackHandler;
import com.prsc.gs.phandler.client.BankHandler;
import com.prsc.gs.phandler.client.ChatHandler;
import com.prsc.gs.phandler.client.CommandHandler;
import com.prsc.gs.phandler.client.DropHandler;
import com.prsc.gs.phandler.client.DuelHandler;
import com.prsc.gs.phandler.client.ExceptionHandler;
import com.prsc.gs.phandler.client.FollowRequest;
import com.prsc.gs.phandler.client.FriendHandler;
import com.prsc.gs.phandler.client.GameSettingHandler;
import com.prsc.gs.phandler.client.InvActionHandler;
import com.prsc.gs.phandler.client.InvUseOnGroundItem;
import com.prsc.gs.phandler.client.InvUseOnItem;
import com.prsc.gs.phandler.client.InvUseOnNpc;
import com.prsc.gs.phandler.client.InvUseOnObject;
import com.prsc.gs.phandler.client.InvUseOnPlayer;
import com.prsc.gs.phandler.client.MenuReplyHandler;
import com.prsc.gs.phandler.client.NpcCommand;
import com.prsc.gs.phandler.client.ObjectAction;
import com.prsc.gs.phandler.client.PickupItem;
import com.prsc.gs.phandler.client.Ping;
import com.prsc.gs.phandler.client.PlayerAppearanceIDHandler;
import com.prsc.gs.phandler.client.PlayerAppearanceUpdater;
import com.prsc.gs.phandler.client.PlayerLogin;
import com.prsc.gs.phandler.client.PlayerLogout;
import com.prsc.gs.phandler.client.PlayerLogoutRequest;
import com.prsc.gs.phandler.client.PrayerHandler;
import com.prsc.gs.phandler.client.PrivacySettingHandler;
import com.prsc.gs.phandler.client.ReportHandler;
import com.prsc.gs.phandler.client.SessionRequest;
import com.prsc.gs.phandler.client.ShopHandler;
import com.prsc.gs.phandler.client.Sleepword;
import com.prsc.gs.phandler.client.SmithingHandler;
import com.prsc.gs.phandler.client.SpellHandler;
import com.prsc.gs.phandler.client.StyleHandler;
import com.prsc.gs.phandler.client.TalkToNpcHandler;
import com.prsc.gs.phandler.client.TradeHandler;
import com.prsc.gs.phandler.client.WalkRequest;
import com.prsc.gs.phandler.client.WallObjectAction;
import com.prsc.gs.phandler.client.WieldHandler;
import com.prsc.gs.service.Service;

/**
 * @author Hikilaka
 * @version 1
 * 
 * Loading packet handlers, trying to be less dependent
 * on external libraries :)
 */
public final class PacketHandlers extends Service {
	
	private final Map<Integer, PacketHandler> gameHandlers = new HashMap<>();

	private final Map<Integer, PacketHandler> loginHandlers = new HashMap<>();
	
	@Override
	public void start() {
		/**
		 * add packet handlers here
		 */
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
		registerGameHandler(new WalkRequest(), 132, 246);
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
		registerGameHandler(new Sleepword(), 200);
		registerGameHandler(new SmithingHandler(), 201, 202);
		registerGameHandler(new SpellHandler(), 206, 55, 71, 49, 67, 17, 104, 232);
		registerGameHandler(new PlayerAppearanceUpdater(), 218);
		registerGameHandler(new WallObjectAction(), 235, 126);
		registerGameHandler(new PickupItem(), 245);
		registerGameHandler(new ShopHandler(), 253, 128, 255);
	}
	
	public void registerGameHandler(PacketHandler handler, int... ids) {
		for (int i : ids) {
			gameHandlers.put(i, handler);
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
