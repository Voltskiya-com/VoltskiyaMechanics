package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@AllArgsConstructor
@Getter
public class Thirst {
    public static final int MAX_THIRST = 1000;
    public static final int MIN_THIRST = 0;

    private final Player player;
    private int thirst;
    private boolean isThirsty;

    public void updateThirst() {
        if (!isThirsty)
            return;
        if (MIN_THIRST < thirst)
            thirst = Math.max(MIN_THIRST, thirst - ThirstConfig.get().getThirstRate());
        List<PotionEffect> effectsToAdd = ThirstConfig.get().getPotionEffects(thirst);
        if (!effectsToAdd.isEmpty())
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> player.addPotionEffects(effectsToAdd));
    }

    public void reset() {
        thirst = MAX_THIRST;
    }

    public boolean toggleIsThirsty() {
        return isThirsty = !isThirsty;
    }

    public double getThirstPercentage() {
        return thirst / (double) MAX_THIRST;
    }

    public void drink(int consumeAmount, boolean isDirty) {
        if (isDirty)
            player.addPotionEffects(ThirstConfig.get().getDirtyEffects());
        thirst = Math.max(MIN_THIRST, Math.min(MAX_THIRST, thirst + consumeAmount));
    }

    public boolean shouldDisableSprint() {
        return 100 > thirst;
    }
}
