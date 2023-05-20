package com.voltskiya.mechanics.food.util;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.food.FoodDecayModule;
import com.voltskiya.mechanics.food.config.FoodRotConfig;
import org.bukkit.NamespacedKey;

public interface FoodHandler {

    NamespacedKey DECAY_AMOUNT_KEY = VoltskiyaPlugin.get().namespacedKey("rotting.food.amount");
    NamespacedKey DECAY_LAST_UPDATED_TIMESTAMP_KEY = VoltskiyaPlugin.get().namespacedKey("rotting.food.last_updated");

    default FoodRotConfig foodConfig() {
        return FoodRotConfig.get();
    }

    default long getTime() {
        return FoodDecayModule.MAIN_WORLD.getGameTime();
    }
}
