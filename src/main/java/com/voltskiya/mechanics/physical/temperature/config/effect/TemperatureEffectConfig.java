package com.voltskiya.mechanics.physical.temperature.config.effect;

import com.voltskiya.lib.configs.data.config.init.AppleConfigInit;
import com.voltskiya.mechanics.physical.temperature.Temperature;
import java.util.ArrayList;
import java.util.List;

public class TemperatureEffectConfig extends AppleConfigInit {

    private static TemperatureEffectConfig instance;
    protected final List<TemperatureEffect> effects = new ArrayList<>();

    public TemperatureEffectConfig() {
        instance = this;
    }

    public static TemperatureEffectConfig get() {
        return instance;
    }

    @Override
    public void onInitConfig() {
        effects.sort(TemperatureEffect.COMPARATOR);
        save();
    }

    public List<TemperatureEffect> findEffects(Temperature temperature) {
        synchronized (effects) {
            return effects.stream()
                .filter(effect -> effect.shouldActivate(temperature))
                .toList();
        }
    }
}
