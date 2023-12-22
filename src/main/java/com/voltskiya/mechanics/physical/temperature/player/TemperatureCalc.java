package com.voltskiya.mechanics.physical.temperature.player;

import com.voltskiya.mechanics.physical.player.PhysicalPlayer;
import com.voltskiya.mechanics.physical.temperature.Temperature;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts;
import java.util.Arrays;
import org.bukkit.entity.Player;

public class TemperatureCalc {

    private final ClothingCalc clothing = new ClothingCalc();
    private final PhysicalPlayer physical;
    /**
     * how inside the player is - close to 1 means very enclosed
     * <p/>
     * [0, 1]
     */
    private final MovingAverage insideness = new MovingAverage(5 * 10);
    /**
     * raw value from weather
     * <p/>
     * (-inf, inf)
     */
    private double airTemp;
    /**
     * todo
     */
    private double airTempFinalized;
    /**
     * raw value from weather
     * <p/>
     * (-inf, inf)
     */
    private double windKph;
    /**
     * [0, 1] - accounts for the following:
     * <ol>
     *     <li>maxWindSpeed</li>
     *     <li>insideness</li>
     *     <li>clothing</li>
     * </ol>>
     */
    private double windImpactFinalized;
    /**
     * (-inf, inf) - accounts for the following:
     * <ol>
     *     <li>insideness</li>
     * </ol>
     */
    private double heatSources;
    /**
     * [0, 100] - accounts for the following:
     * <ol>
     *     <li>Rain/snow</li>
     *     <li>partially in water</li>
     *     <li>underwater</li>
     *     <li>Clothing</li>
     * </ol>
     */
    private double wetness;
    private double dryingRate;
    private double heatTransferRate;
    private double evaporationRate;

    public TemperatureCalc(PhysicalPlayer physical) {
        this.physical = physical;
        this.clothing.calculate(getPlayer());
    }

    private static TemperatureConsts consts() {
        return TemperatureConsts.get();
    }

    public void updateClothing() {
        this.clothing.calculate(getPlayer());
    }

    private Temperature temperature() {
        return this.physical.getTemperature();
    }

    public ClothingCalc getClothing() {
        return this.clothing;
    }

    public Player getPlayer() {
        return physical.getPlayer();
    }

    public double getFinalAirTemp() {
        return this.airTempFinalized;
    }

    public void setAirTemp(double airTemp) {
        this.airTemp = airTemp;
    }

    /**
     * used for decreasing/increasing wetness value
     *
     * @return the ambient airTemp without considering clothing
     */
    public double getAmbientAirTemp() {
        return this.airTemp + this.heatSources;
    }

    public void finalizeAirTemp() {
        double realWetness = temperature().getWetness() / consts().wetness.maxWetness;
        double wetnessScaler = consts().temperature.evaporationScalerToHeat * evaporationRate;

        double protection;
        if (this.airTemp > 0) {
            this.airTempFinalized = this.airTemp * (1 - this.windImpactFinalized);
            protection = clothing.getHeatProtection();
        } else {
            this.airTempFinalized = this.airTemp * (1 + this.windImpactFinalized);
            double wetImpactOnClothing = consts().wetness.impactOnWinterClothing(windImpactFinalized);

            double clothingMultiplier = 1 - wetImpactOnClothing;
            double wetnessMultiplier = wetImpactOnClothing * (1 - realWetness);
            double multiplier = clothingMultiplier + wetnessMultiplier; // between 0 and 1
            protection = clothing.getColdProtection() * multiplier;
        }
        this.airTempFinalized += this.heatSources - wetnessScaler;

        // if protection 0, temperature stays the same
        if (protection < 0) {
            double prot = -protection / 100; // flip the sign
            this.airTempFinalized *= 1 + 3 * prot; // if (protection == -100), will 4x the temperature
        } else {
            double prot = protection / 100; // already positive
            this.airTempFinalized *= 1 - prot; // if (protection == 100), will zero the temperature
        }
    }

    public void setWindKph(double windKph) {
        this.windKph = windKph;
    }

    public double finalizeWindKph() {
        this.windImpactFinalized = windKph / TemperatureConsts.get().maxWindSpeed;
        double protection = clothing.getWindProtection();
        if (protection < 0) {
            double prot = -protection / 100; // flip the sign
            this.windImpactFinalized *= 1 + 3 * prot; // if (protection == -100), will 4x the temperature
        } else {
            double prot = protection / 100; // already positive
            this.windImpactFinalized *= 1 - prot; // if (protection == 100), will zero the temperature
        }
        this.windImpactFinalized *= 1 - getInsideness(); // very inside means very protected
        return windImpactFinalized * TemperatureConsts.get().maxWindSpeed;
    }

    /**
     * how inside the player is - close to 1 means very enclosed
     * <p/>
     * [0, 1]
     */
    public double getInsideness() {
        return this.insideness.get();
    }

    public void setInsideness(double insideness) {
        this.insideness.addVal(insideness);
    }

    public void setHeatSources(double heatSources) {
        this.heatSources = heatSources;
    }

    public double getFinalWetness() {
        return this.wetness;
    }

    public void setWetness(double wetness) {
        this.wetness = wetness;
    }

    public void finalizeDryingRate() {
        double ambientAir = getAmbientAirTemp();
        this.evaporationRate = consts().wetness.baseDryingPerSec;
        double windImpactOnDrying = consts().wetness.windImpactOnDrying;
        this.evaporationRate *= 1 + windImpactOnDrying * this.windImpactFinalized;
        this.dryingRate = this.evaporationRate;
        if (ambientAir > 0) {
            double heatImpactOnDrying = consts().wetness.heatImpactOnDrying;
            double effectiveMaxAirTemp = consts().temperature.effectiveMaxAirTemp;
            double airTempImpact = Math.min(1, ambientAir / effectiveMaxAirTemp);
            this.dryingRate *= 1 + heatImpactOnDrying * airTempImpact;
        }
    }

    public double getFinalDryingRate() {
        return dryingRate;
    }

    public void finalizeHeatTransferRate() {
        this.heatTransferRate = consts().temperature.baseHeatTransferPerSec;

        double evaporation = consts().temperature.evaporationImpactOnHeatRate * this.evaporationRate;
        double realWetness = this.temperature().getWetness() / 100;
        double evaporationImpact = Math.min(realWetness, evaporation);

        double direction = this.temperature().getHeatDirection();
        boolean isCooling = direction < 0;
        if (isCooling) {
            // cooling
            this.heatTransferRate *= this.clothing.getColdResistance();
            this.heatTransferRate *= 1 + evaporationImpact;
        } else {
            this.heatTransferRate *= this.clothing.getHeatResistance();
            this.heatTransferRate *= 1 - evaporationImpact;
        }
        if (heatTransferRate < 0) {
            heatTransferRate = 0;
            return;
        }
        if (isCooling) heatTransferRate = -heatTransferRate;

        // if we're returning to normal from extreme temperatures, return to 0 quickly
        double heat = temperature().getTemperature();
        if (Math.abs(heat + direction) < Math.abs(heat)) {
            heatTransferRate *= consts().temperature.returnToZeroEffect * Math.abs(heat);
        }
    }

    public double getFinalHeatTransferRate() {
        return this.heatTransferRate;
    }

    public double getFinalSoakRate() {
        return consts().wetness.baseSoakPerSec * clothing.getWetResistance();
    }

    /**
     * may return a value that would make the player exceed max/min limits of wetness
     */
    public double getFinalWetTransferRate() {
        boolean isDrying = temperature().getWetnessDirection() <= 0;
        double soakRateOrDry = isDrying ? 0 : this.getFinalSoakRate(); // drying
        return soakRateOrDry - this.getFinalDryingRate();
    }

    private static class MovingAverage {

        private final double[] values;
        private final int size;
        private boolean isNewAverage = true;
        private int index = 0;

        private MovingAverage(int size) {
            this.values = new double[size];
            this.size = size;
        }

        private void addVal(double val) {
            if (this.isNewAverage) {
                Arrays.fill(this.values, val);
                this.isNewAverage = false;
                return;
            }
            this.values[index] = val;
            this.index = (index + 1) % size;
        }

        public double get() {
            return Arrays.stream(values).average().orElse(0);
        }
    }
}
