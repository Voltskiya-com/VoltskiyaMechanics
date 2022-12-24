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
            VoltskiyaPlayer voltskiyaPlayer = VoltskiyaPlayer.getPlayer(player);
            switch (e.getExhaustionReason()) {
                case JUMP_SPRINT -> voltskiyaPlayer.increaseStamina(StaminaConfig.get().getSprintJumpingIncrement());
                case JUMP -> voltskiyaPlayer.increaseStamina(StaminaConfig.get().getJumpingIncrement());
                case SWIM -> voltskiyaPlayer.increaseStamina(StaminaConfig.get().getSwimmingIncrement());
                case SPRINT -> voltskiyaPlayer.increaseStamina(StaminaConfig.get().getSprintingIncrement());
                case CROUCH -> voltskiyaPlayer.increaseStamina(StaminaConfig.get().getCrouchingIncrement());
                case WALK, WALK_ON_WATER, WALK_UNDERWATER -> voltskiyaPlayer.increaseStamina(StaminaConfig.get().getWalkingIncrement());
            }
        }
    }
}
