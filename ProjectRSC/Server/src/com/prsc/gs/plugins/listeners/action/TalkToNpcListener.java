package com.prsc.gs.plugins.listeners.action;


import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface TalkToNpcListener {
    /**
     * Called when a player talks to a npc
     *
     * @param p
     * @param n
     */
    public void onTalkToNpc(Player p, Npc n);

}
