package org.darkquest.gs.plugins;

import org.darkquest.gs.model.Player;

public interface QuestInterface {

    public int getQuestId();

    public String getQuestName();

    public boolean isMembers();

    public void handleReward(Player player);

}
