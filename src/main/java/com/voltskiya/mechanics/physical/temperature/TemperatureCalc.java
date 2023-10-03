package com.voltskiya.mechanics.physical.temperature;

import com.voltskiya.mechanics.physical.player.PhysicalPlayer;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts.HeatConsts;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts.WetnessConsts;
import org.bukkit.entity.Player;

public class TemperatureCalc {

    private final ClothingCalc clothing = new ClothingCalc();
    private final PhysicalPlayer physical;
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
     * how inside the player is - close to 1 means very enclosed
     * <p/>
     * [0, 1]
     */
    private double insideness;
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

    private static WetnessConsts constsWetness() {
        return TemperatureConsts.get().wetness;
    }

    private static HeatConsts constsTemperature() {
        return TemperatureConsts.get().temperature;
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
        double realWetness = temperature().getWetness() / constsWetness().maxWetness;
        double protection;

        if (this.airTempFinalized > 0) {
            this.airTempFinalized = this.airTemp * (1 - this.windImpactFinalized);
            protection = Math.max(clothing.getHeatProtection(), realWetness * 100);
        } else {
            this.airTempFinalized = this.airTemp * (1 + this.windImpactFinalized);
            double wetImpactOnClothing = constsWetness().impactOnWinterClothing(windImpactFinalized);

            double clothingMultiplier = 1 - wetImpactOnClothing;
            double wetnessMultiplier = wetImpactOnClothing * (1 - realWetness);
            double multiplier = clothingMultiplier + wetnessMultiplier; // between 0 and 1
            protection = clothing.getColdProtection() * multiplier;
        }
        this.airTempFinalized += this.heatSources;

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

    public void finalizeWindKph() {
        this.windImpactFinalized = windKph;
        this.windImpactFinalized /= TemperatureConsts.get().maxWindSpeed;
        this.windImpactFinalized /= clothing.getWindProtection();
        this.windImpactFinalized *= 1 - insideness; // very inside means very protected
    }

    public double getInsideness() {
        return insideness;
    }

    public void setInsideness(double insideness) {
        this.insideness = insideness;
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
        this.evaporationRate = constsWetness().baseDryingPerSec;
        double windImpactOnDrying = constsWetness().windImpactOnDrying;
        this.evaporationRate *= 1 + windImpactOnDrying * this.windImpactFinalized;
        this.dryingRate = this.evaporationRate;
        if (ambientAir > 0) {
            double heatImpactOnDrying = constsWetness().heatImpactOnDrying;
            double effectiveMaxAirTemp = constsTemperature().effectiveMaxAirTemp;
            double airTempImpact = Math.min(1, ambientAir / effectiveMaxAirTemp);
            this.dryingRate *= 1 + heatImpactOnDrying * airTempImpact;
        }
    }

    public double getFinalDryingRate() {
        return dryingRate;
    }

    public void finalizeHeatTransferRate() {
        this.heatTransferRate = constsTemperature().baseHeatTransferPerSec;
        if (this.temperature().getHeatDirection() < 0) {
            // cooling
            this.heatTransferRate *= this.clothing.getColdResistance();
        } else {
            this.heatTransferRate *= this.clothing.getHeatResistance();
        }
        this.heatTransferRate += constsTemperature().evaporationImpactOnHeatRate * this.evaporationRate;
    }

    public double getFinalHeatTransferRate() {
        return this.heatTransferRate;
    }

    public double getFinalSoakRate() {
        return constsWetness().baseSoakPerSec * clothing.getWetResistance();
    }

    /**
     * may return a value that would make the player go exceed max/min limits of wetness
     */
    public double getFinalWetTransferRate() {
        if (temperature().getWetDirection() < 0) {
            // drying
            return this.getFinalDryingRate();
        }
        return this.getFinalSoakRate() - this.getFinalDryingRate();
    }
}
