package com.voltskiya.mechanics.stamina;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class Stamina {
    public static final int MAX_STAMINA = 10_000;
    public static final int MIN_STAMINA = 0;

    private final Player player;
    private int stamina;
    private boolean outOfStamina;

    public void increaseStamina(int amount) {
        stamina = Math.max(MIN_STAMINA, Math.min(MAX_STAMINA, stamina + amount));
        if (MIN_STAMINA == stamina)
            outOfStamina = true;
        else if (outOfStamina && StaminaConfig.get().getRunAgainThreshold() < stamina)
            outOfStamina = false;
    }

    public void updateStamina() {
        if (0 == ((CraftPlayer) player).getHandle().walkDistO)
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
}
