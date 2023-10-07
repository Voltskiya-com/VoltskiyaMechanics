package com.voltskiya.mechanics.physical.temperature.config.clothing;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public interface HasClothingProtection {

    Material getMaterial();

    @NotNull
    default EquipmentSlot getSlot() {
        return getMaterial().getEquipmentSlot();
    }

    /**
     * decrease the max
     * <p/>
     * if (protection == -100), will 4x the windImpact
     * <p/>
     * if (protection ==  100), will zero the wind
     */
    double getWindProtection();

    /**
     * (1,100) divide wet by wetProtection
     */
    double getWetProtection();

    /**
     * positive value slows the rate
     * <p/>
     * -100 => 4x rate; +100 => 0 rate;
     */
    double getWetResistance();

    /**
     * decrease the max
     * <p/>
     * if (protection == -100), will 4x the temperature
     * <p/>
     * if (protection ==  100), will zero the temperature
     */
    double getHeatProtection();

    /**
     * positive value slows the rate
     * <p/>
     * -100 => 4x rate; +100 => 0 rate;
     */
    double getHeatResistance();

    /**
     * decrease the max
     * <p/>
     * if (protection == -100), will 4x the temperature
     * <p/>
     * if (protection ==  100), will zero the temperature
     */
    double getColdProtection();

    /**
     * positive value slows the rate
     * <p/>
     * -100 => 4x rate; +100 => 0 rate;
     */
    double getColdResistance();

}
