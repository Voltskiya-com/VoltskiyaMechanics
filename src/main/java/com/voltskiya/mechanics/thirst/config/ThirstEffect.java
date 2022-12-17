package com.voltskiya.mechanics.thirst.config;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ThirstEffect {
    private static final int DEFAULT_DURATION = 40;
    private final String potion;
    private final int amplifier;
    private final int thirstLowerBound;
    private final int thirstUpperBound;

    public ThirstEffect() {
        potion = PotionEffectType.HERO_OF_THE_VILLAGE.getKey().asString();
        amplifier = 0;
        thirstLowerBound = -1;
        thirstUpperBound = -1;
    }

    public ThirstEffect(PotionEffectType potion, int amplifier, int thirstLowerBound, int thirstUpperBound) {
        this.potion = potion.getKey().asString();
        this.amplifier = amplifier;
        this.thirstLowerBound = thirstLowerBound;
        this.thirstUpperBound = thirstUpperBound;
    }

    @NotNull
    public PotionEffect potion() {
        PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.fromString(potion));
        if (null == effectType) {
            log.error("{} is not a valid PotionEffectType in ThirstEffect", potion);
            return PotionEffectType.HERO_OF_THE_VILLAGE.createEffect(0, 0);
        }
        return new PotionEffect(effectType, 40, amplifier, true);
    }

    public boolean shouldActivate(int thirstLevel) {
        return thirstLowerBound <= thirstLevel && thirstLevel <= thirstUpperBound;
    }
}
