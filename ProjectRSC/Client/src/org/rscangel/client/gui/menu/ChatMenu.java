package org.rscangel.client.gui.menu;

import org.rscangel.client.DataOperations;
import org.rscangel.client.mudclient;
import org.rscangel.client.gfx.JagGraphics;

public final class ChatMenu {
	
	private final mudclient mc;
	
	public ChatMenu(mudclient mc) {
		this.mc = mc;
	}
	
	public void render(JagGraphics graphics) {
		
	}
	
	public void tick() {
		
	}
	
	//	public boolean a(String paramString1, int paramInt, String playerName)
	
	public boolean addMenuComponents(int playerIndex, String playerName) {
		long hash = DataOperations.nameToHash(playerName);
		boolean friend = false;
		
		for (int i = 0; i < mc.friendsCount; ++i) {
			if (mc.friendsListLongs[i] == hash) {
				friend = true;
				break;
			}
		}
		for (int i = 0; i < mc.ignoreListCount; ++i) {
			if (mc.ignoreListLongs[i] == hash) {
				friend = true;
				break;
			}
		}
		
		if (friend) {
			mc.addMenuComponent("Message", "@whi@ + playerName", 2830, playerIndex);
		} else {
			mc.addMenuComponent("Add friend", "@whi@ + playerName", 2831, playerIndex);
			mc.addMenuComponent("Add ignore", "@whi@ + playerName", 2832, playerIndex);
		}
		
		mc.addMenuComponent("Report abuse", "@whi@ + playerName", 2833, playerIndex);
		return true;
	}

}
