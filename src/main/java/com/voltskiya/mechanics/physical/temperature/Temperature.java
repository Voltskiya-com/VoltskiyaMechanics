package com.voltskiya.mechanics.physical.temperature;

import static com.voltskiya.mechanics.physical.temperature.player.TemperatureTickMath.doTickMath;

import com.voltskiya.mechanics.physical.player.ConfigPotionEffect;
import com.voltskiya.mechanics.physical.player.PhysicalPlayer;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerPart;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureChecks;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts;
import com.voltskiya.mechanics.physical.temperature.config.biome.TemperatureBiome;
import com.voltskiya.mechanics.physical.temperature.config.biome.TemperatureBiomeDB;
import com.voltskiya.mechanics.physical.temperature.config.biome.time.MergedTemperatureTime;
import com.voltskiya.mechanics.physical.temperature.config.biome.time.TemperatureTime;
import com.voltskiya.mechanics.physical.temperature.config.effect.TemperatureEffect;
import com.voltskiya.mechanics.physical.temperature.config.effect.TemperatureEffectConfig;
import com.voltskiya.mechanics.physical.temperature.player.TemperatureCalc;
import com.voltskiya.mechanics.physical.temperature.player.TemperatureVisual;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Temperature extends PhysicalPlayerPart {

    private final transient Map<UUID, Integer> effectlastApplied = new HashMap<>();
    // range: [-inf, inf]
    protected double temperature = 0;
    // range: [0, 100]
    protected double wetness = 0;
    protected transient double wind;
    private transient TemperatureCalc calc;
    private transient TemperatureVisual visual;
    private double lastHeatDirection;

    private static TemperatureConsts consts() {
        return TemperatureConsts.get();
    }


    @Override
    protected void onLoad(PhysicalPlayer player) {
        super.onLoad(player);
        this.calc = new TemperatureCalc(this.getPhysical());
        this.visual = new TemperatureVisual(this.getPlayer());
    }

    @Override
    public Player getPlayer() {
        return super.getPlayer();
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

    public double getWind() {
        return this.wind;
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

        this.wind = calc.finalizeWindKph();
        calc.finalizeDryingRate();
        calc.finalizeAirTemp();
        calc.finalizeHeatTransferRate();

        this.doWetTick();
        this.doHeatTick();

        this.addTemperatureEffects();
        this.visuals();
    }

    private void visuals() {
        double wetAmount = this.getWetness() / consts().wetness.maxWetness;
        int freezeTicks = calcFreezeTicks();
        double windChance = calcWindChance();

        this.visual.wetAmount(wetAmount)
            .freezeTicks(freezeTicks)
            .setWind(windChance);
    }

    private int calcFreezeTicks() {
        double maxTemp = consts().temperature.effectiveMaxAirTemp;
        int maxFreezeTicks = this.getPlayer().getMaxFreezeTicks();
        double freezeTicks = (-this.getTemperature() - 30);
        freezeTicks *= maxFreezeTicks * 2 / maxTemp;

        if (freezeTicks < 0) freezeTicks = 0;
        else if (freezeTicks > maxFreezeTicks) freezeTicks = maxFreezeTicks;
        return (int) freezeTicks;
    }

    private double calcWindChance() {
        return getWind() / consts().maxWindSpeed;
    }

    public double getHeatDirection() {
        return calc.getFinalAirTemp() - this.temperature;
    }

    public double getLastHeatDirection() {
        return this.lastHeatDirection;
    }

    private void doHeatTick() {
        double rate = calc.getFinalHeatTransferRate();
        double goal = calc.getFinalAirTemp();
        double max = consts().temperature.effectiveMaxAirTemp;

        this.lastHeatDirection = doTickMath(rate, this.temperature, goal, max);
        this.temperature += lastHeatDirection;
    }

    public double getWetnessDirection() {
        return calc.getFinalWetness() - this.wetness;
    }

    private void doWetTick() {
        double rate = calc.getFinalWetTransferRate();
        double goal = calc.getFinalWetness();
        double max = consts().wetness.maxWetness;

        this.wetness += doTickMath(rate, this.wetness, goal, max);

        if (this.wetness < 0) this.wetness = 0;
        else if (this.wetness > max) this.wetness = max;
    }

    public void addTemperatureEffects() {
        List<TemperatureEffect> effects = TemperatureEffectConfig.get().findEffects(this);
        int now = Bukkit.getCurrentTick();
        for (TemperatureEffect effect : effects) {
            this.effectlastApplied.put(effect.getId(), now);
        }
        getPlayer().addPotionEffects(effects.stream().map(ConfigPotionEffect::potion).toList());
    }


    public int getEffectLastActivated(UUID id) {
        return effectlastApplied.getOrDefault(id, 0);
    }
}
