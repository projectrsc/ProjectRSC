package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Npc;

import org.darkquest.gs.model.Player;

public interface TalkToNpcListener {
    /**
     * Called when a player talks to a npc
     *
     * @param p
     * @param n
     */
    public void onTalkToNpc(Player p, Npc n);

}
