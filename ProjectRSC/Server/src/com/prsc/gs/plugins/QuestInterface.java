package com.prsc.gs.plugins;

import com.prsc.gs.model.Player;

public interface QuestInterface {

    public int getQuestId();

    public String getQuestName();

    public boolean isMembers();
    
    public void handleReward(Player player);
}
