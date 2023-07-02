package com.voltskiya.mechanics.physical.player;

import org.bukkit.entity.Player;

public abstract class PhysicalPlayerPart {

    private transient PhysicalPlayer player;

    protected void onLoad(PhysicalPlayer player) {
        this.player = player;
    }

    public abstract void onTick();

    protected Player getPlayer() {
        return this.player.getPlayer();
    }

    protected PhysicalPlayer getPhysical() {
        return this.player;
    }

    protected abstract void onDeath();
}
