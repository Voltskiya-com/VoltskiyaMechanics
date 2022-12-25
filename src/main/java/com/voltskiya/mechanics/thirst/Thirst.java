package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.player.VoltskiyaPlayer;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.potion.PotionEffect;

@NoArgsConstructor
@Getter
public class Thirst implements HasVoltPlayer {

    public static final int MAX_THIRST = 1000;
    public static final int MIN_THIRST = 0;

    private transient VoltskiyaPlayer voltPlayer;
    private int thirst = MAX_THIRST;
    private boolean isThirsty = true;

    public void load(VoltskiyaPlayer volt) {
        voltPlayer = volt;
    }

    public void updateThirst() {
        if (!isThirsty)
            return;
        if (MIN_THIRST < thirst)
            thirst = Math.max(MIN_THIRST, thirst - ThirstConfig.get().getThirstRate());
        List<PotionEffect> effectsToAdd = ThirstConfig.get().getPotionEffects(thirst);
        if (!effectsToAdd.isEmpty())
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> playerIfPresent((p) -> p.addPotionEffects(effectsToAdd)));
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
            playerIfPresent((p) -> p.addPotionEffects(ThirstConfig.get().getDirtyEffects()));
        thirst = Math.max(MIN_THIRST, Math.min(MAX_THIRST, thirst + consumeAmount));
    }

    public boolean shouldDisableSprint() {
        return 100 > thirst;//TODO add to config
    }
}
