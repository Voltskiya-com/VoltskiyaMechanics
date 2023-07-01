package com.voltskiya.mechanics.physical.thirst.config.effect;

import com.voltskiya.mechanics.physical.PhysicalModule;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class DirtyWaterPotionEffect {

    protected int amplifier;
    protected int duration;
    protected String potion;
    private PotionEffect cachedPotion;

    public DirtyWaterPotionEffect() {
        amplifier = 0;
        duration = 1;
    }

    public DirtyWaterPotionEffect(PotionEffectType potion, int amplifier, int duration) {
        this.potion = potion.getKey().asString();
        this.amplifier = amplifier;
        this.duration = duration;
    }

    @NotNull
    public PotionEffect potion() {
        if (this.cachedPotion != null) return cachedPotion;
        PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.fromString(potion));
        if (effectType == null) {
            PhysicalModule.get().logger()
                .error(String.format("%s is not a valid PotionEffectType in Thirst.DirtyWaterEffect", potion));
            return new PotionEffect(Map.of());
        }
        return cachedPotion = new PotionEffect(effectType, duration, amplifier);
    }
}

