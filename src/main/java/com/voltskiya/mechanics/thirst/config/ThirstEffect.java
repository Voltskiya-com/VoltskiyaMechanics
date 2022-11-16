package com.voltskiya.mechanics.thirst.config;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ThirstEffect {

    private static final int DEFAULT_DURATION = 40;
    private NamespacedKey potion = PotionEffectType.HERO_OF_THE_VILLAGE.getKey(); // just a random key so the user knows they messed up
    private int amplifier;
    private int thirstLowerBound;
    private int thirstUpperBound;

    public ThirstEffect() {
    }

    public ThirstEffect(PotionEffectType potion, int amplifier, int thirstLowerBound,
        int thirstUpperBound) {
        this.potion = potion.getKey();
        this.amplifier = amplifier;
        this.thirstLowerBound = thirstLowerBound;
        this.thirstUpperBound = thirstUpperBound;
    }

    @NotNull
    public PotionEffect potion() {
        PotionEffectType effectType = PotionEffectType.getByKey(this.potion);
        if (effectType == null) {
            VoltskiyaPlugin.get().getSLF4JLogger().error(
                String.format("%s is not a valid PotionEffectType in ThirstConfig",
                    this.potion.asString()));
            // to fulfill @NotNull
            return PotionEffectType.HERO_OF_THE_VILLAGE.createEffect(0, 0);
        }
        return new PotionEffect(effectType, DEFAULT_DURATION, amplifier, true);
    }

    public boolean shouldActivate(int thirstLevel) {
        return thirstLowerBound <= thirstLevel && thirstLevel <= thirstUpperBound;
    }
}
