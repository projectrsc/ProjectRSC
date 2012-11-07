package org.darkquest.gs.plugins.listeners.action;

import org.darkquest.gs.model.Player;

public interface PlayerAttackListener {

    public void onPlayerAttack(Player p, Player affectedmob);
}
