package com.voltskiya.mechanics.food.config;

import com.voltskiya.lib.configs.data.config.init.AppleConfigInit;
import java.util.HashMap;
import java.util.Map;

public class CoolerConfigDatabase extends AppleConfigInit {

    private static CoolerConfigDatabase instance;
    protected final CoolerConfig playerConfig = new CoolerConfig(1);
    protected final CoolerConfig defaultConfig = new CoolerConfig(0.5);
    protected final Map<Integer, CoolerItemConfig> coolers = new HashMap<>();

    public CoolerConfigDatabase() {
        instance = this;
    }

    public static void init() {
        if (instance.coolers.isEmpty()) {
            add(new CoolerItemConfig(1, "Ice Box", 0.5));
            add(new CoolerItemConfig(5, "Cooler", 0.333));
            add(new CoolerItemConfig(10, "Freezer", 0.25));
            instance.save();
        }
    }

    private static void add(CoolerItemConfig config) {
        instance.coolers.put(config.getId(), config);
    }

    public static CoolerConfigDatabase get() {
        return instance;
    }

    public CoolerItemConfig getConfig(int id) {
        return coolers.get(id);
    }

    public CoolerConfig getPlayerConfig() {
        return playerConfig;
    }

    public CoolerConfig getDefaultConfig() {
        return defaultConfig;
    }
}
