package com.prsc.gs.event;

import com.prsc.gs.model.Player;

public abstract class MiniEvent extends SingleEvent {

    public MiniEvent(Player owner) {
        super(owner, 500);
    }

    public MiniEvent(Player owner, int delay) {
        super(owner, delay);
    }

    public abstract void action();

}