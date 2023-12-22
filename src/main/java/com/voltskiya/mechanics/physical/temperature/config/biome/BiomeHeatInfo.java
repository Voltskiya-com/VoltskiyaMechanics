package com.voltskiya.mechanics.physical.temperature.config.biome;

import com.voltskiya.lib.configs.data.config.init.IAppleConfigInit;
import com.voltskiya.mechanics.physical.temperature.util.daily.DoubleWithFluctuations;
import com.voltskiya.mechanics.physical.temperature.util.daily.VarChangedDaily;
import org.bukkit.World;

public class BiomeHeatInfo implements IAppleConfigInit {

    protected DoubleWithFluctuations degrees = new DoubleWithFluctuations();
    protected transient VarChangedDaily<Double> degreesDaily;

    @Override
    public void onInitConfig() {
        degreesDaily = new VarChangedDaily<>(degrees::randomVariance);
    }

    public double getDegrees(World world) {
        return degreesDaily.get(world);
    }
}
