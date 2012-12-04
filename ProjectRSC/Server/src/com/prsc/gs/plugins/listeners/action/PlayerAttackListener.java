package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

public interface PlayerAttackListener {

    public void onPlayerAttack(Player p, Player affectedmob);
}
