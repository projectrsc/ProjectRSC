package com.prsc.gs.model;

import com.prsc.gs.model.component.world.Area;


public class ViewArea {

    private Mob mob;

    public ViewArea(Mob mob) {
        this.mob = mob;
    }
    
    public Iterable<GameObject> getGameObjectsInView() {
        return Area.getViewableObjects(mob.getLocation(), 21);
    }

    public Iterable<Item> getItemsInView() {
        return Area.getViewableItems(mob.getLocation(), 21);
    }

    public Iterable<Npc> getNpcsInView() {
        return Area.getViewableNpcs(mob.getLocation(), 16);
    }

    public Iterable<Player> getPlayersInView() {
        return Area.getViewablePlayers(mob.getLocation(), 16);
    }

}
