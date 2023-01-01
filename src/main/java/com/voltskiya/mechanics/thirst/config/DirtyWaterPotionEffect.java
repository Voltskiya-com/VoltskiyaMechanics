package com.voltskiya.mechanics.thirst.config;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class DirtyWaterPotionEffect {
    private String potion;
    private final int amplifier;
    private final int duration;

    public DirtyWaterPotionEffect() {
        potion = PotionEffectType.HERO_OF_THE_VILLAGE.getKey().asString();
        amplifier = 0;
        duration = 1;
    }

    public DirtyWaterPotionEffect(PotionEffectType potion, int amplifier, int duration) {
        this.potion = PotionEffectType.HERO_OF_THE_VILLAGE.getKey().asString();
        this.potion = potion.getKey().asString();
        this.amplifier = amplifier;
        this.duration = duration;
    }

    @NotNull
    public PotionEffect potion() {
        PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.fromString(potion));
        if (null == effectType) {
            VoltskiyaPlugin.get().getSLF4JLogger().error(String.format("%s is not a valid PotionEffectType in Thirst.DirtyWaterEffect", potion));
            return new PotionEffect(Map.of());
        } else {
            return new PotionEffect(effectType, duration, amplifier);
        }
    }
}

