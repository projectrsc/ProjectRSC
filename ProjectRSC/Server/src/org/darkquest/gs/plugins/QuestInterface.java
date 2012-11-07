package org.darkquest.gs.plugins;

public interface QuestInterface {

    public int getQuestId();

    public String getQuestName();

    public boolean isMembers();
    
    public void handleReward();
}
