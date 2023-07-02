package com.voltskiya.mechanics.physical.stamina;

import com.voltskiya.mechanics.physical.player.PhysicalPlayer;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerPart;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Stamina extends PhysicalPlayerPart {

    public static final int MAX_STAMINA = 20_000;
    public static final int NO_REGEN_AT_0_STAMINA_TICKS = 5 * 20;
    public static final PotionEffect LOW_SPRINT_EFFECT = PotionEffectType.HUNGER.createEffect(30, 0);
    protected int stamina = MAX_STAMINA;
    protected int noRegenTimer = 0;
    private transient boolean isJumping = false;
    private transient Location lastLocation;

    public void addStamina(int amount) {
        if (noRegenTimer != 0) return;

        stamina += amount;
        if (stamina > MAX_STAMINA) stamina = MAX_STAMINA;
        if (stamina < 0) stamina = 0;

        if (stamina == 0) {
            noRegenTimer = NO_REGEN_AT_0_STAMINA_TICKS;
        }
    }

    @Override
    public void onLoad(PhysicalPlayer player) {
        super.onLoad(player);
        lastLocation = player.getPlayer().getLocation();
    }

    @Override
    public void onTick() {
        if (updateLocation()) return;
        if (this.noRegenTimer != 0) this.noRegenTimer--;
        int increment = calcStaminaIncrement();
        addStamina(increment);
        isJumping = false;
        getPhysical().verifySprint();
    }

    private boolean updateLocation() {
        Location location = getPlayer().getLocation();
        boolean isSameWorld = location.getWorld().getUID().equals(lastLocation.getWorld().getUID());
        if (!isSameWorld) {
            lastLocation = location;
            return true;
        }
        return false;
    }


    private int calcStaminaIncrement() {
        Player player = this.getPlayer();
        Location location = player.getLocation();
        boolean isStanding = location.distanceSquared(lastLocation) == 0;
        lastLocation = location;
        boolean isJumping = this.isJumping();
        boolean isSprinting = player.isSprinting();
        boolean isSwimming = player.isSwimming();
        boolean isSneaking = player.isSneaking();
        StaminaConfig config = StaminaConfig.get();
        if (isJumping && isSprinting) return config.sprintJumpingIncrement;
        else if (isJumping) return config.jumpingIncrement;
        else if (isSwimming) return config.swimmingIncrement;
        else if (isSneaking) return config.crouchingIncrement;
        else if (isSprinting) return config.sprintingIncrement;
        else if (isStanding) return config.standingStillIncrement;
        else return config.walkingIncrement;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    @Override
    protected void onDeath() {
        this.stamina = MAX_STAMINA;
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
