package com.voltskiya.mechanics.physical.temperature.config.effect;

import com.voltskiya.mechanics.physical.PhysicalModule;
import com.voltskiya.mechanics.physical.player.ConfigPotionEffect;
import com.voltskiya.mechanics.physical.temperature.Temperature;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;

public class TemperatureEffect extends ConfigPotionEffect {

    public static final Comparator<TemperatureEffect> COMPARATOR = Comparator.comparingInt(
        t -> Objects.requireNonNullElse(t.activationLowerBound, Integer.MIN_VALUE));
    private final transient Random random = new Random();
    protected UUID id = UUID.randomUUID();
    protected Integer activationLowerBound;
    protected Integer activationUpperBound;
    protected int interval;

    public boolean shouldActivate(Temperature temperature) {
        double heat = temperature.getTemperature();
        if (activationUpperBound != null && heat >= activationUpperBound) return false;
        if (activationLowerBound != null && activationLowerBound > heat) return false;

        int lastActivated = temperature.getEffectLastActivated(this.id);
        if (Bukkit.getCurrentTick() - lastActivated < interval / 2) return false;

        if (interval > PhysicalModule.TICKS_PER_INCREMENT * 2) {
            int variation = random.nextInt(interval / PhysicalModule.TICKS_PER_INCREMENT);
            if (variation != 0) return false;
        }
        PotionEffect active = temperature.getPlayer().getPotionEffect(potion().getType());
        return active == null || active.getDuration() < duration();
    }


    public UUID getId() {
        return this.id;
    }
}
