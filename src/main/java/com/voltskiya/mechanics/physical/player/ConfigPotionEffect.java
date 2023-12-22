package com.voltskiya.mechanics.physical.player;

import com.voltskiya.mechanics.physical.PhysicalModule;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ConfigPotionEffect {

    protected String potion;
    protected int amplifier;
    protected Integer duration = null;

    private transient PotionEffect cachedPotion;

    public ConfigPotionEffect() {
        potion = PotionEffectType.HERO_OF_THE_VILLAGE.getKey().asString();
        amplifier = 0;
    }

    public ConfigPotionEffect(PotionEffectType potion, int amplifier) {
        this.potion = potion.getKey().asString();
        this.amplifier = amplifier;
    }

    @NotNull
    public PotionEffect potion() {
        if (cachedPotion != null) return cachedPotion;
        PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.fromString(potion));
        if (null == effectType) {
            PhysicalModule.get().logger().error("%s is not a valid PotionEffectType in ThirstEffect".formatted(potion));
            return this.cachedPotion = PotionEffectType.HERO_OF_THE_VILLAGE.createEffect(0, 0);
        }
        return this.cachedPotion = new PotionEffect(effectType, duration(), amplifier, true);
    }

    public int duration() {
        return Objects.requireNonNullElse(this.duration, 40);
    }
}
