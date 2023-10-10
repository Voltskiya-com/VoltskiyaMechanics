package com.voltskiya.mechanics.physical.temperature.player;

import com.voltskiya.mechanics.physical.temperature.config.clothing.ClothingConfig;
import com.voltskiya.mechanics.physical.temperature.config.clothing.HasClothingProtection;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClothingCalc implements HasClothingProtection {

    protected double windProtection = 0; // [-100, 100]
    protected double wetProtection = 1; // [1, 100]
    // slow the rate
    protected double wetResistance = 0; // [-100, 100]
    protected double heatProtection = 0; // [-100, 100]
    protected double heatResistance = 0; // [-100, 100]
    protected double coldProtection = 0; // [-100, 100]
    protected double coldResistance = 0; // [-100, 100]

    protected ItemStack[] previousArmor = new ItemStack[0];

    private static double resistance(double resistance) {
        return 1 - resistance / 100;
    }

    private void resetStats() {
        this.wetProtection = this.windProtection = 1;
        this.wetResistance = this.heatProtection = this.heatResistance = this.coldProtection = this.coldResistance = 0;
    }

    public void calculate(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
//        if (Arrays.deepEquals(previousArmor, armor)) return;
//        previousArmor = armor;

        this.resetStats();
        ResistanceMath windProtection = new ResistanceMath();
        ResistanceMath wetResistance = new ResistanceMath();
        ResistanceMath heatProtection = new ResistanceMath();
        ResistanceMath heatResistance = new ResistanceMath();
        ResistanceMath coldProtection = new ResistanceMath();
        ResistanceMath coldResistance = new ResistanceMath();
        for (ItemStack item : armor) {
            HasClothingProtection clothing = ClothingConfig.get().getClothing(item);
            windProtection.add(clothing.getWindProtection());
            this.wetProtection *= 1 + (clothing.getWetProtection() - 1) / 100;
            wetResistance.add(clothing.getWetResistance());
            heatProtection.add(clothing.getHeatProtection());
            heatResistance.add(clothing.getHeatResistance());
            coldProtection.add(clothing.getColdProtection());
            coldResistance.add(clothing.getColdResistance());
        }
        this.windProtection = windProtection.get();
        this.wetProtection = (this.wetProtection - 1) * 100 + 1;
        this.wetResistance = wetResistance.get();
        this.heatProtection = heatProtection.get();
        this.heatResistance = heatResistance.get();
        this.coldProtection = coldProtection.get();
        this.coldResistance = coldResistance.get();
    }

    @Override
    public Material getMaterial() {
        throw new NotImplementedException("Material of ClothingCalc always null");
    }

    public double getWindProtection() {
        return windProtection;
    }

    public double getWetProtection() {
        return wetProtection;
    }

    public double getWetResistance() {
        return resistance(wetResistance);
    }

    public double getHeatProtection() {
        return heatProtection;
    }

    public double getHeatResistance() {
        return resistance(heatResistance);
    }

    public double getColdProtection() {
        return coldProtection;
    }

    public double getColdResistance() {
        return resistance(coldResistance);// if (resistance == -100), will 4x the rate
    }

    private static class ResistanceMath {

        private double positive = 1;
        private double negative = 1;

        public void add(double resistance) {
            if (resistance > 0) {
                positive *= 1 + resistance / 100;
            } else if (resistance < 0) {
                negative *= 1 - resistance / 100;
            }
        }

        public double get() {
            return (getPos() - getNeg()) * 100;
        }

        private double getPos() {
            return 1 - 1 / positive;
        }

        private double getNeg() {
            return 1 - 1 / negative;
        }
    }
}
