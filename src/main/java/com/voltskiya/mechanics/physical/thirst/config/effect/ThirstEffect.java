package com.voltskiya.mechanics.physical.thirst.config.effect;

import com.voltskiya.mechanics.physical.PhysicalModule;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ThirstEffect {

    private static final int DEFAULT_DURATION = 40;
    private final String potion;
    private final int amplifier;
    private final int thirstLowerBound;
    private final int thirstUpperBound;
    private transient PotionEffect cachedPotion;

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
        if (cachedPotion != null) return cachedPotion;
        PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.fromString(potion));
        if (null == effectType) {
            PhysicalModule.get().logger().error("%s is not a valid PotionEffectType in ThirstEffect".formatted(potion));
            return this.cachedPotion = PotionEffectType.HERO_OF_THE_VILLAGE.createEffect(0, 0);
        }
        return this.cachedPotion = new PotionEffect(effectType, 40, amplifier, true);
    }

    public boolean shouldActivate(Player player, int thirstLevel) {
        PotionEffect active = player.getPotionEffect(this.potion().getType());
        if (active != null && active.getDuration() > PhysicalModule.TICKS_PER_INCREMENT) return false;
        return thirstLowerBound <= thirstLevel && thirstLevel <= thirstUpperBound;
    }
}
