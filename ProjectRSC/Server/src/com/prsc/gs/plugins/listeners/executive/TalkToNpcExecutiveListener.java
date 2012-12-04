package com.prsc.gs.plugins.listeners.executive;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface TalkToNpcExecutiveListener {
    /**
     * Return true to block a player from talking to a npc
     */
    public boolean blockTalkToNpc(Player p, Npc n);
}
