package org.darkquest.gs.plugins;

import org.darkquest.gs.model.ChatMessage;
import org.darkquest.gs.model.Mob;
import org.darkquest.gs.model.Player;

public abstract class Quest implements QuestInterface {

    public void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sleep() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void playMessages(Player player, Mob mob, boolean playerIsSpeaker, String... messages) {
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
