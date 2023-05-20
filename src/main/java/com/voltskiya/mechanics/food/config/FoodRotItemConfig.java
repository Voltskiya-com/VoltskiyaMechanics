package com.voltskiya.mechanics.food.config;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class FoodRotItemConfig {

    protected Material material;
    protected int ticksToDecay;
    protected Material rotInto = null;

    public FoodRotItemConfig(Material material, int ticksToDecay) {
        this.material = material;
        this.ticksToDecay = ticksToDecay;
    }

    public int getTicksToDecay() {
        return ticksToDecay;
    }

    public Material getMaterial() {
        return material;
    }

    @Nullable
    public Material getRotInto() {
        return rotInto;
    }

    public void setDecayInto(Material decayResult) {
        this.rotInto = decayResult;
    }
}
