package com.voltskiya.mechanics.stamina;

import com.voltskiya.lib.AbstractModule;
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

    public NamespacedKey getKey(String key) {
        return getPlugin().namespacedKey(getName() + "." + key);
    }

    public static NamespacedKey key(String key) {
        return get().getKey(key);
    }
}
