package com.voltskiya.mechanics.food.config;

import com.voltskiya.lib.configs.data.config.init.AppleConfigInit;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class FoodRotConfig extends AppleConfigInit {

    private static FoodRotConfig instance;
    protected Map<Material, FoodRotItemConfig> foodItems = new HashMap<>();

    public FoodRotConfig() {
        instance = this;
    }

    public static FoodRotConfig get() {
        return instance;
    }

    @Nullable
    public FoodRotItemConfig getFoodTimer(Material material) {
        return foodItems.get(material);
    }

    public boolean hasFoodTimer(Material material) {
        return getFoodTimer(material) != null;
    }
}
