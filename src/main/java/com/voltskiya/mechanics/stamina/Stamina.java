package com.voltskiya.mechanics.stamina;

import com.voltskiya.mechanics.player.VoltskiyaPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Stamina {

    public static final int MAX_STAMINA = 10_000;
    public static final int NO_REGEN_AT_0_STAMINA_TICKS = 5 * 20;
    public static final PotionEffect LOW_SPRINT_EFFECT = PotionEffectType.HUNGER.createEffect(30, 0);
    private transient VoltskiyaPlayer player;
    private transient Location lastLocation;
    private int stamina = MAX_STAMINA;
    private int noRegenTimer = 0;
    private transient boolean isJumping = false;

    public void addStamina(int amount) {
        if (noRegenTimer != 0) return;

        stamina += amount;
        if (stamina > MAX_STAMINA) stamina = MAX_STAMINA;
        if (stamina < 0) stamina = 0;

        if (stamina == 0) {
            noRegenTimer = NO_REGEN_AT_0_STAMINA_TICKS;
        }
    }

    public void onLoad(VoltskiyaPlayer player) {
        this.player = player;
        lastLocation = player.getPlayer().getLocation();
    }

    public void onTick() {
        if (updateLocation()) return;
        if (this.noRegenTimer != 0) this.noRegenTimer--;
        int increment = calcStaminaIncrement();
        addStamina(increment);
        isJumping = false;
        player.verifySprint();
    }

    private boolean updateLocation() {
        Location location = player.getPlayer().getLocation();
        boolean isSameWorld = location.getWorld().getUID().equals(lastLocation.getWorld().getUID());
        if (!isSameWorld) {
            lastLocation = location;
            return true;
        }
        return false;
    }

    private int calcStaminaIncrement() {
        Player player = this.player.getPlayer();
        Location location = player.getLocation();

        boolean isStanding = location.distanceSquared(lastLocation) == 0;
        lastLocation = location;
        boolean isJumping = this.isJumping();
        boolean isSprinting = player.isSprinting();
        boolean isSwimming = player.isSwimming();
        boolean isSneaking = player.isSneaking();
        StaminaConfig config = StaminaConfig.get();
        if (isJumping && isSprinting) return config.getSprintJumpingIncrement();
        else if (isJumping) return config.getJumpingIncrement();
        else if (isSwimming) return config.getSwimmingIncrement();
        else if (isSneaking) return config.getCrouchingIncrement();
        else if (isStanding) return config.getStandingStillIncrement();
        else if (isSprinting) return config.getSprintingIncrement();
        else return config.getWalkingIncrement();
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void onDeath() {
        stamina = MAX_STAMINA;
    }

    public double getStaminaPercentage() {
        return stamina / (double) MAX_STAMINA;
    }

    public boolean isLowSprint() {
        return 0 == stamina;
    }

    public void setJumping() {
        this.isJumping = true;
    }
}
