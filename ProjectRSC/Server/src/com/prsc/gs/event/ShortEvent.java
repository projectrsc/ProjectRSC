package com.prsc.gs.event;

import com.prsc.gs.model.Player;

public abstract class ShortEvent extends SingleEvent {

    public ShortEvent(Player owner) {
        super(owner, 1500);
    }

    public abstract void action();

}