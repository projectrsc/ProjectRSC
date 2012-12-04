package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Npc;
import com.prsc.gs.model.Player;

public interface PlayerKilledNpcListener {
    public void onPlayerKilledNpc(Player p, Npc n);
}
