package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Npc;
import org.darkquest.gs.model.Player;

public interface PlayerKilledNpcListener {
    public void onPlayerKilledNpc(Player p, Npc n);
}
