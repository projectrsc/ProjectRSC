package org.darkquest.gs.plugins;

import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.Mob;
import org.darkquest.gs.model.Player;

public abstract class Scriptable {
		
	protected void sleep(int milliseconds) {	
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void playMessages(Player player, Mob mob, boolean playerIsSpeaker, String... messages) {
		for (String message : messages) {
			if (playerIsSpeaker) {
				player.informGroupOfChatMessage(new ChatMessage(player, message, mob));
			} else {
				player.informGroupOfNpcMessage(new ChatMessage(mob, message, player));
			}
			sleep(2200);
		}
	}
	
}
