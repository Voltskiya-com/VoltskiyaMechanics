package com.voltskiya.mechanics.stamina;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import java.util.List;
import org.bukkit.NamespacedKey;

public class StaminaModule extends AbstractModule {
    private static StaminaModule instance;

    public static StaminaModule get() {
        return instance;
    }

    public StaminaModule() {
        instance = this;
    }

    @Override
    public void enable() {
        getPlugin().registerEvents(new StaminaListener());
    }

    @Override
    public String getName() {
        return "Stamina";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(configJson(StaminaConfig.class,"StaminaConfig"));
    }

    public NamespacedKey getKey(String key) {
        return getPlugin().namespacedKey(getName() + "." + key);
    }

    public static NamespacedKey key(String key) {
        return get().getKey(key);
    }
}
