package org.darkquest.gs.event.impl;

import org.darkquest.gs.event.DelayedEvent;

import java.util.Calendar;

public abstract class DateEvent extends DelayedEvent {

    public DateEvent(Calendar d) {
        super(null, (int) (d.getTimeInMillis() - System.currentTimeMillis()));
    }

    public abstract void action();

    public void run() {
        action();
        super.matchRunning = false;
    }

}