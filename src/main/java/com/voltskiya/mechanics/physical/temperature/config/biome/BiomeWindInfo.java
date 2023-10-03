package com.voltskiya.mechanics.physical.temperature.config.biome;

import com.voltskiya.lib.configs.data.config.init.AppleConfigInit;
import com.voltskiya.mechanics.physical.temperature.util.daily.DoubleWithFluctuations;
import com.voltskiya.mechanics.physical.temperature.util.daily.VarChangedDaily;
import org.bukkit.World;

public class BiomeWindInfo extends AppleConfigInit {

    protected DoubleWithFluctuations kph = new DoubleWithFluctuations();
    protected transient VarChangedDaily<Double> kphDaily;

    @Override
    public void onInitConfig() {
        kphDaily = new VarChangedDaily<>(kph::randomVariance);
    }

    public double getKph(World world) {
        return kphDaily.get(world);
    }
}
