package com.voltskiya.mechanics.physical.stamina;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.player.VoltskiyaPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason;

public class StaminaListener implements Listener {

    public StaminaListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }


    @EventHandler
    public void onJump(EntityExhaustionEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        Stamina stamina = VoltskiyaPlayerManager.getPlayer(player).getStamina();
        ExhaustionReason reason = e.getExhaustionReason();
        if (reason == ExhaustionReason.JUMP_SPRINT || reason == ExhaustionReason.JUMP) {
            stamina.setJumping();
        }
    }


}
