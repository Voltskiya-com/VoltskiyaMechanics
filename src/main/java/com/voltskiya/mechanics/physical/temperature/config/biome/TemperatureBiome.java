package com.voltskiya.mechanics.physical.temperature.config.biome;

import com.voltskiya.lib.configs.data.config.init.IAppleConfigInit;
import com.voltskiya.mechanics.physical.temperature.config.biome.time.MergedTemperatureTime;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

public class TemperatureBiome implements IAppleConfigInit {

    protected String name;
    protected NamespacedKey minecraft;
    protected TemperatureTimeMapping<BiomeHeatInfo> dailyTemperatures = new TemperatureTimeMapping<>(new BiomeHeatInfo());
    protected TemperatureTimeMapping<BiomeWindInfo> dailyWind = new TemperatureTimeMapping<>(new BiomeWindInfo());

    public static TemperatureBiome fallbackBiome() {
        TemperatureBiome biome = new TemperatureBiome();
        biome.name = "fallback";
        return biome;
    }

    @Override
    public void onInitConfig() {
        dailyTemperatures.toEach(BiomeHeatInfo::onInitConfig);
        dailyWind.toEach(BiomeWindInfo::onInitConfig);
    }

    public NamespacedKey getMinecraft() {
        return minecraft;
    }

    public double getHeatNow(MergedTemperatureTime mcTime, World world) {
        return dailyTemperatures.get(mcTime,
            (perc, heat) -> heat.getDegrees(world) * perc,
            (a, b, c) -> a + b + c);
    }

    public double getWindKph(MergedTemperatureTime mcTime, World world) {
        return dailyWind.get(mcTime,
            (perc, wind) -> wind.getKph(world) * perc,
            (a, b, c) -> a + b + c
        );
    }
}
