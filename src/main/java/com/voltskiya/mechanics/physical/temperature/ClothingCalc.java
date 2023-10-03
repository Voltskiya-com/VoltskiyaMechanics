package com.voltskiya.mechanics.physical.temperature;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClothingCalc {

    // (1,100) divide wind by windProtection
    // decrease the maximum
    protected double windProtection = 1; // [1, 100]
    protected double wetProtection = 1; // [1, 100]
    // slow the rate
    protected double wetResistance = 0; // [0, 100]
    protected double heatProtection = 1; // [0, 100]
    protected double heatResistance = 0; // [0, 100]
    protected double coldProtection = 1; // [0, 100]
    protected double coldResistance = 0; // [0, 100]

    private static double resistance(double resistance) {
        return 1 - resistance / 100;
    }

    public void calculate(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack item : armor) {
            if (item == null) continue;
        }
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
        return resistance(this.coldResistance);// if (resistance == -100), will 4x the temperature
    }
}
