package com.voltskiya.mechanics.stamina;

import com.voltskiya.mechanics.player.HasVoltPlayer;
import com.voltskiya.mechanics.player.VoltskiyaPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;

public class Stamina implements HasVoltPlayer {

    public static final int MAX_STAMINA = 10_000;
    public static final int MIN_STAMINA = 0;

    private int stamina = MAX_STAMINA;
    private boolean outOfStamina = false;
    private transient VoltskiyaPlayer voltPlayer;

    public void increaseStamina(int amount) {
        stamina = Math.max(MIN_STAMINA, Math.min(MAX_STAMINA, stamina + amount));
        if (MIN_STAMINA == stamina)
            outOfStamina = true;
        else if (outOfStamina && StaminaConfig.get().getRunAgainThreshold() < stamina)
            outOfStamina = false;
    }

    public void load(VoltskiyaPlayer voltPlayer) {
        this.voltPlayer = voltPlayer;
    }

    public void updateStamina() {
        final Float walkDist = player().getOrDefault(p -> ((CraftPlayer) p).getHandle().walkDistO, 0f);
        if (0f == walkDist)
            increaseStamina(StaminaConfig.get().getStandingStillIncrement());
    }

    public void reset() {
        stamina = MAX_STAMINA;
        outOfStamina = false;
    }

    public double getStaminaPercentage() {
        return stamina / (double) MAX_STAMINA;
    }

    public boolean shouldDisableSprint() {
        return outOfStamina;
    }

    @Override
    public VoltskiyaPlayer getVolt() {
        return this.voltPlayer;
    }

}
