package com.voltskiya.mechanics.physical.thirst;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.thirst.config.ThirstConfig;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class Thirst {

    public static final int MAX_THIRST = 100000;
    public static final int MIN_THIRST = 0;

    private transient Player player;
    private int thirst = MAX_THIRST;
    private boolean isThirsty = true;

    public Thirst() {
    }

    public void onLoad(Player player) {
        this.player = player;
    }

    public void onTick() {
        if (!isThirsty)
            return;
        if (MIN_THIRST < thirst)
            thirst = Math.max(MIN_THIRST, thirst - ThirstConfig.get().getThirstRate());
        List<PotionEffect> effectsToAdd = ThirstConfig.get().getPotionEffects(thirst);
        if (!effectsToAdd.isEmpty())
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> player.addPotionEffects(effectsToAdd));
    }

    public void onDeath() {
        thirst = MAX_THIRST;
    }

    public boolean toggleIsThirsty() {
        return isThirsty = !isThirsty;
    }

    public double getThirstPercentage() {
        return thirst / (double) MAX_THIRST;
    }


    public void drink(int consumeAmount) {
        thirst = Math.max(MIN_THIRST, Math.min(MAX_THIRST, thirst + consumeAmount));
    }

    public boolean shouldDisableSprint() {
        return 100 > thirst;//TODO add to config
    }
}
