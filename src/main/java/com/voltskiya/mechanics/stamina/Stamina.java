package com.voltskiya.mechanics.stamina;

import com.voltskiya.mechanics.player.VoltskiyaPlayer;
import org.bukkit.Location;

public class Stamina {

    public static final int MAX_STAMINA = 10_000;
    public static final int MIN_STAMINA = 0;

    private transient VoltskiyaPlayer player;
    private transient Location lastLocation;

    private int stamina = MAX_STAMINA;
    private boolean outOfStamina;

    public void increaseStamina(int amount) {
        stamina = Math.max(MIN_STAMINA, Math.min(MAX_STAMINA, stamina + amount));
        if (MIN_STAMINA == stamina) {
            outOfStamina = true;
            if (player.getPlayer().isSprinting())
                player.onSprint();
        }
        else if (outOfStamina && StaminaConfig.get().getRunAgainThreshold() < stamina)
            outOfStamina = false;
    }

    public void onLoad(VoltskiyaPlayer player) {
        this.player = player;
        lastLocation = player.getPlayer().getLocation();
    }

    public void onTick() {
        Location location = player.getPlayer().getLocation();
        if (0 == location.distanceSquared(lastLocation))
            increaseStamina(StaminaConfig.get().getStandingStillIncrement());
        lastLocation = location;
    }

    public void onDeath() {
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
