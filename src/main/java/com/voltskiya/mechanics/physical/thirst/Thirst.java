package com.voltskiya.mechanics.physical.thirst;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerPart;
import com.voltskiya.mechanics.physical.thirst.config.ThirstConfig;
import java.util.List;
import org.bukkit.potion.PotionEffect;

public class Thirst extends PhysicalPlayerPart {

    public static final int MAX_THIRST = 100000;
    public static final int MIN_THIRST = 0;

    private int thirst = MAX_THIRST;
    private boolean isThirsty = true;

    public Thirst() {
    }

    @Override
    public void onTick() {
        if (!isThirsty)
            return;
        if (MIN_THIRST < thirst)
            thirst = Math.max(MIN_THIRST, thirst - ThirstConfig.get().getThirstRate());
        List<PotionEffect> effectsToAdd = ThirstConfig.get().getPotionEffects(getPlayer(), thirst);
        if (!effectsToAdd.isEmpty())
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> getPlayer().addPotionEffects(effectsToAdd));
    }

    @Override
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
