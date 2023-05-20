package com.voltskiya.mechanics.food.config;

public class CoolerItemConfig extends CoolerConfig {

    protected String displayName;
    protected int id;

    public CoolerItemConfig(int id, String displayName, double rotMultiplier) {
        super(rotMultiplier);
        this.id = id;
        this.displayName = displayName;
    }

    public CoolerItemConfig() {
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getId() {
        return this.id;
    }
}
