package org.darkquest.gs.plugins;

import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.Mob;
import org.darkquest.gs.model.Player;

@Deprecated
public abstract class Scriptable { 

	/**
	 * Helper methods 
	 * @param delay
	 */
	
	public void sleep(final int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}

	@Deprecated
	protected void playMessages(Player player, Mob mob, boolean playerIsSpeaker, String... messages) {
		for (String message : messages) {
			if(!message.equalsIgnoreCase("null")) {
				if (playerIsSpeaker) {
					player.informGroupOfChatMessage(new ChatMessage(player, message, mob));
				} else {
					player.informGroupOfNpcMessage(new ChatMessage(mob, message, player));
				}
			}
			sleep(2200);
		}
	} 
}
