package com.voltskiya.mechanics.stamina;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import java.util.List;
import org.bukkit.NamespacedKey;

public class StaminaModule extends AbstractModule {

    private static StaminaModule instance;

    public StaminaModule() {
        instance = this;
    }

    public static StaminaModule get() {
        return instance;
    }

    @Override
    public void enable() {
        new StaminaListener();
    }

    @Override
    public String getName() {
        return "Stamina";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(configJson(StaminaConfig.class, "StaminaConfig"));
    }

    public NamespacedKey getKey(String key) {
        return getPlugin().namespacedKey(getName() + "." + key);
    }
}
