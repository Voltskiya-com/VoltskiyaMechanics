package com.voltskiya.mechanics.food.config;

public class CoolerConfig {

    protected double rotMultiplier = 1;

    public CoolerConfig(double rotMultiplier) {
        this.rotMultiplier = rotMultiplier;
    }

    public CoolerConfig() {
    }

    public double getRotMultiplier() {
        return rotMultiplier;
    }

}
