package com.voltskiya.mechanics.physical.temperature.config.biome;

import com.voltskiya.lib.configs.data.config.init.IAppleConfigInit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

public class TemperatureBiomeDB implements IAppleConfigInit {


    private static TemperatureBiomeDB instance;
    protected transient Map<NamespacedKey, TemperatureBiome> minecraftToTemperature = new HashMap<>();
    protected Map<String, TemperatureBiome> biomes = new HashMap<>();
    protected TemperatureBiome fallback = TemperatureBiome.fallbackBiome();

    public TemperatureBiomeDB() {
        instance = this;
    }

    public static TemperatureBiomeDB get() {
        return instance;
    }

    @Override
    public void onInitConfig() {
        minecraftToTemperature = biomes.values().stream()
            .collect(Collectors.toMap(TemperatureBiome::getMinecraft, Function.identity()));
        minecraftToTemperature.values().forEach(TemperatureBiome::onInitConfig);
        fallback.onInitConfig();
    }

    @NotNull
    public TemperatureBiome getBiomeOrFallback(Biome minecraftBiome) {
        return minecraftToTemperature.getOrDefault(minecraftBiome.getKey(), fallback);
    }
}
