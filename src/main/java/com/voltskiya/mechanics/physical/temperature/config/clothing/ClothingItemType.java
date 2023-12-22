package com.voltskiya.mechanics.physical.temperature.config.clothing;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

public class ClothingItemType implements HasClothingProtection {

    protected Material material;

    protected double windProtection = 0; // [-100, 100]
    protected double wetProtection = 1; // [1, 100]
    protected double wetResistance = 0; // [-100, 100]
    protected double heatProtection = 0; // [-100, 100]
    protected double heatResistance = 0; // [-100, 100]
    protected double coldProtection = 0; // [-100, 100]

    protected double coldResistance = 0; // [-100, 100]


    public ClothingItemType() {
    }

    public ClothingItemType(Material material, ClothingSet set) {
        this.material = material;
        EquipmentSlot slot = getSlot();
        this.windProtection = set.getWindProtection(slot);
        this.wetProtection = set.getWetProtection(slot);
        this.wetResistance = set.getWetResistance(slot);
        this.heatProtection = set.getHeatProtection(slot);
        this.heatResistance = set.getHeatResistance(slot);
        this.coldProtection = set.getColdProtection(slot);
        this.coldResistance = set.getColdResistance(slot);
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public double getWindProtection() {
        return windProtection;
    }

    @Override
    public double getWetProtection() {
        return wetProtection;
    }

    @Override
    public double getWetResistance() {
        return wetResistance;
    }

    @Override
    public double getHeatProtection() {
        return heatProtection;
    }

    @Override
    public double getHeatResistance() {
        return heatResistance;
    }

    @Override
    public double getColdProtection() {
        return coldProtection;
    }

    @Override
    public double getColdResistance() {
        return coldResistance;
    }
}

