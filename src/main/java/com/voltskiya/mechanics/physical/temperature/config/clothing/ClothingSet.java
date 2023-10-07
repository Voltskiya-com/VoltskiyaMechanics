package com.voltskiya.mechanics.physical.temperature.config.clothing;

import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class ClothingSet {

    protected Material[] material = new Material[0];
    @Nullable
    protected Map<EquipmentSlot, Double> equipmentRatios;


    // (1,100) divide wind by windProtection
    // decrease the maximum
    protected double windProtection = 1; // [1, 100]
    // (1,100) divide wind by windProtection
    // decrease the maximum
    protected double wetProtection = 1; // [1, 100]
    /**
     * positive value slows the rate
     * <p/>
     * -100 => 4x rate; +100 => 0 rate;
     */
    protected double wetResistance = 0; // [-100, 100]
    // decrease the max
    protected double heatProtection = 0; // [-100, 100]
    /**
     * positive value slows the rate
     * <p/>
     * -100 => 4x rate; +100 => 0 rate;
     */
    protected double heatResistance = 0; // [-100, 100]
    // decrease the max
    protected double coldProtection = 0; // [-100, 100]
    /**
     * positive value slows the rate
     * <p/>
     * -100 => 4x rate; +100 => 0 rate;
     */
    protected double coldResistance = 0; // [-100, 100]


    public ClothingItemType[] listItems() {
        ClothingItemType[] clothing = new ClothingItemType[material.length];
        for (int i = 0; i < material.length; i++)
            clothing[i] = new ClothingItemType(material[i], this);
        return clothing;
    }

    public double getWindProtection(EquipmentSlot slot) {
        return windProtection * getRatio(slot);
    }

    public double getWetProtection(EquipmentSlot slot) {
        return Math.max(1, wetProtection * getRatio(slot));
    }

    public double getWetResistance(EquipmentSlot slot) {
        return wetResistance * getRatio(slot);
    }

    public double getHeatProtection(EquipmentSlot slot) {
        return heatProtection * getRatio(slot);
    }

    public double getHeatResistance(EquipmentSlot slot) {
        return heatResistance * getRatio(slot);
    }

    public double getColdProtection(EquipmentSlot slot) {
        return coldProtection * getRatio(slot);
    }

    public double getColdResistance(EquipmentSlot slot) {
        return coldResistance * getRatio(slot);
    }

    private double getRatio(EquipmentSlot slot) {
        Map<EquipmentSlot, Double> fallback = ClothingConfig.get().fallbackEquipmentRatios;
        return Objects.requireNonNullElse(this.equipmentRatios, fallback)
            .getOrDefault(slot, 1d);
    }
}
