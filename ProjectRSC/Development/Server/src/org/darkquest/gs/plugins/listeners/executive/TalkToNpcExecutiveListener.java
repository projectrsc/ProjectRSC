package org.darkquest.gs.plugins.listeners.executive;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;

public interface TalkToNpcExecutiveListener {
    /**
     * Return true to block a player from talking to a npc
     */
    public boolean blockTalkToNpc(Player p, Npc n);
}
