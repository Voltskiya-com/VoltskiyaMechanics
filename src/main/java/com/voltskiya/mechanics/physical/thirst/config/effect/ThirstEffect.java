package com.voltskiya.mechanics.physical.thirst.config.effect;

import com.voltskiya.mechanics.physical.PhysicalModule;
import com.voltskiya.mechanics.physical.player.ConfigPotionEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ThirstEffect extends ConfigPotionEffect {

    protected int thirstLowerBound;
    protected int thirstUpperBound;

    public ThirstEffect() {
        thirstLowerBound = -1;
        thirstUpperBound = -1;
    }

    public ThirstEffect(PotionEffectType potion, int amplifier, int thirstLowerBound, int thirstUpperBound) {
        super(potion, amplifier);
        this.thirstLowerBound = thirstLowerBound;
        this.thirstUpperBound = thirstUpperBound;
    }


    public boolean shouldActivate(Player player, int thirstLevel) {
        PotionEffect active = player.getPotionEffect(this.potion().getType());
        if (active != null && active.getDuration() > PhysicalModule.TICKS_PER_INCREMENT) return false;
        return thirstLowerBound <= thirstLevel && thirstLevel <= thirstUpperBound;
    }
}
