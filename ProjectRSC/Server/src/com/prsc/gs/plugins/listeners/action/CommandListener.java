package com.prsc.gs.plugins.listeners.action;

import com.prsc.gs.model.Player;

public interface CommandListener {
    public void onCommand(String command, String[] args, Player player);
}
