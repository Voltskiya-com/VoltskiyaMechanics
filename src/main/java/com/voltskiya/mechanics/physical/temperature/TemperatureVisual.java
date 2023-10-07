package com.voltskiya.mechanics.physical.temperature;

import org.bukkit.entity.Player;

public class TemperatureVisual {

    private final Player player;
    private int freezeTicks;

    public TemperatureVisual(Player player) {

        this.player = player;
    }

    public void setFreezeTicks(int freezeTicks) {
        this.freezeTicks = freezeTicks;
    }
}
