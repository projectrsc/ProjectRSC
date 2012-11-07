package org.darkquest.gs.plugins;

public abstract class Quest extends Scriptable implements QuestInterface {
	
	public static final byte BEGINNING = 0x0;
	public static final byte COMPLETE = -1;
	
	public int getQuestStage() {
		return participant.getQuestStage(this);
	}
	
	public void setQuestStage(int stage) {
		this.participant.setQuestStage(this, stage);
	}
	
	public void setQuestCompleted() {
		participant.sendQuestComplete(getQuestId());
		setQuestStage(COMPLETE);
	}

    public void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sleep() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
