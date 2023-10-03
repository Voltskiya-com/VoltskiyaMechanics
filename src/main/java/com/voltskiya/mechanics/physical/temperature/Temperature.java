package com.voltskiya.mechanics.physical.temperature;

import com.voltskiya.mechanics.physical.player.PhysicalPlayer;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerPart;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureChecks;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts;
import com.voltskiya.mechanics.physical.temperature.config.biome.TemperatureBiome;
import com.voltskiya.mechanics.physical.temperature.config.biome.TemperatureBiomeDB;
import com.voltskiya.mechanics.physical.temperature.config.biome.time.MergedTemperatureTime;
import com.voltskiya.mechanics.physical.temperature.config.biome.time.TemperatureTime;
import com.voltskiya.mechanics.physical.temperature.config.effect.TemperatureEffect;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Temperature extends PhysicalPlayerPart {

    // range: [-inf, inf]
    protected double temperature = 0;
    // range: [0, 100]
    protected double wetness = 0;
    protected transient TemperatureCalc calc;

    private static TemperatureConsts consts() {
        return TemperatureConsts.get();
    }

    @Override
    protected void onLoad(PhysicalPlayer player) {
        super.onLoad(player);
        calc = new TemperatureCalc(this.getPhysical());
    }

    @Override
    protected void onDeath() {
        this.temperature = 0;
        this.wetness = 0;
    }

    public double getTemperature() {
        return temperature;
    }

    /**
     * range [0, maxWetness]
     *
     * @return the current wetness
     */
    public double getWetness() {
        return wetness;
    }

    @Override
    public void onTick() {
        Player player = this.getPlayer();
        calc.updateClothing();
        Location location = player.getEyeLocation();
        World world = location.getWorld();
        Biome minecraftBiome = location.getBlock().getComputedBiome();
        @Nullable TemperatureBiome currentBiome = TemperatureBiomeDB.get().getBiomeOrFallback(minecraftBiome);
        MergedTemperatureTime time = TemperatureTime.getTime(world.getTime());
        calc.setAirTemp(currentBiome.getHeatNow(time, world));
        calc.setWindKph(currentBiome.getWindKph(time, world));
        calc.setInsideness(TemperatureChecks.insideness(location)); // not too burdensome
        calc.setHeatSources(TemperatureChecks.blockSources(calc));
        calc.setWetness(TemperatureChecks.wetness(calc));

        calc.finalizeWindKph();
        calc.finalizeAirTemp();
        calc.finalizeDryingRate();
        calc.finalizeHeatTransferRate();

        this.doWetTick();
        this.doTemperatureTick();

    }

    public double getHeatDirection() {
        return calc.getFinalAirTemp() - this.temperature;
    }

    private void doTemperatureTick() {
        this.temperature += getHeatDirection() * calc.getFinalHeatTransferRate();
    }

    public double getWetDirection() {
        return calc.getFinalWetness() - this.wetness;
    }

    private void doWetTick() {
        this.wetness += getWetDirection() * calc.getFinalWetTransferRate();
        System.out.println(this.getWetDirection());
        System.out.println(this.wetness);
        double maxWetness = consts().wetness.maxWetness;
        if (wetness < 0) wetness = 0;
        else if (wetness > maxWetness) wetness = maxWetness;
    }

    public void doTemperatureChecks() {
//        Location location = this.getPlayer().getLocation();
//        Biome minecraftBiome = location.getBlock().getComputedBiome();
//        @Nullable TemperatureBiome currentBiome = TemperatureBiomeDB.get().getBiomeOrFallback(minecraftBiome);
//        double airTemp = currentBiome.getTypicalTempNow(location.getWorld().getTime());
//        double insideness = TemperatureChecks.insideness(location);
//        double blockHeatSource = TemperatureChecks.sources(location);
//        double wind = TemperatureChecks.wind(currentBiome, location);
//        double wetness = TemperatureChecks.wetness(player);
//
//        TemperatureChecks.ClothingTemperature clothing = TemperatureChecks.clothing(player);
//        double finalBlockHeatSource = (1 + insideness) * blockHeatSource;
//        double finalWind = clothing.resistWind(wind * (1 - insideness));
//        double finalWetness = clothing.resistWet(wetness);
//        double playerWetness = this.playerInfo.doWetTick(finalWetness);
//
//        double airTemp2 = airTemp + finalBlockHeatSource;
//
//        double fluidFactor = TemperatureChecks.fluidFactor(finalWind, playerWetness);
//        double boundaries = 150;
//        double airTemp3 = airTemp2 - (
//            (boundaries / (1 + Math.pow(Math.E, (-airTemp2 / boundaries)))) * fluidFactor / 10);
//
//        double feltTemperature = clothing.resistTemp(airTemp3);
//        this.playerInfo.temperature += (feltTemperature - this.playerInfo.temperature)
//            * TmwWatchConfig.getCheckInterval().heatTransferConstant;
//        this.playerInfo.doTemperatureEffects(this.playerVisual);
//        TextComponent msg = new TextComponent();
//        msg.setText(String.format("final temp - %.2f, biome - %s", this.playerInfo.temperature,
//            currentBiome == null ? "null" : currentBiome.getName()));
//        this.player.sendActionBar(msg);
//        if (--this.saveInterval <= 0) {
//            this.saveInterval = SAVE_INTERVAL;
//            this.playerInfo.saveThreaded();
//        }
    }

    public void addTemperatureEffects() {
//        if (this.temperature < -30) {
//            playerVisual.setFreezeTicks((int) (Math.abs(this.temperature + 30)));
//        }
        List<TemperatureEffect> effects = new ArrayList<>();

    }


}
