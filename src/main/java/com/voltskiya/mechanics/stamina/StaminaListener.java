package com.voltskiya.mechanics.stamina;

import com.voltskiya.mechanics.VoltskiyaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;

public class StaminaListener implements Listener {

    @EventHandler
    public void onExhaust(EntityExhaustionEvent e) {
        if (e.getEntity() instanceof Player player) {
            Stamina stamina = VoltskiyaPlayer.getPlayer(player).getStamina();
            switch (e.getExhaustionReason()) {
                case JUMP_SPRINT -> stamina.increaseStamina(StaminaConfig.get().getSprintJumpingIncrement());
                case JUMP -> stamina.increaseStamina(StaminaConfig.get().getJumpingIncrement());
                case SWIM -> stamina.increaseStamina(StaminaConfig.get().getSwimmingIncrement());
                case SPRINT -> stamina.increaseStamina(StaminaConfig.get().getSprintingIncrement());
                case CROUCH -> stamina.increaseStamina(StaminaConfig.get().getCrouchingIncrement());
                case WALK, WALK_ON_WATER, WALK_UNDERWATER -> stamina.increaseStamina(StaminaConfig.get().getWalkingIncrement());
            }
        }
    }
}
