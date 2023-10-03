package com.voltskiya.mechanics.physical.temperature.config.blocks;

import com.voltskiya.lib.configs.data.config.init.AppleConfigInit;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class TemperatureBlocksConfig extends AppleConfigInit {

    private static TemperatureBlocksConfig instance;
    protected HashMap<String, TemperatureBlock> blocks = new HashMap<>();

    public TemperatureBlocksConfig() {
        instance = this;
    }

    public static TemperatureBlocksConfig get() {
        return instance;
    }

    @Nullable
    public TemperatureBlock getBlock(Material type) {
        return blocks.get(type.name());
    }

    public synchronized void addTempBlock(TemperatureBlock tempBlock) {
        blocks.put(tempBlock.getName(), tempBlock);
        save();
    }

    public List<TemperatureBlock> listBlocks() {
        return blocks.values().stream().toList();
    }
}

