package com.voltskiya.mechanics.physical.player;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.PhysicalRecipes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import voltskiya.apple.utilities.minecraft.player.PlayerUtils;

public class PhysicalPlayerListener implements Listener {

    public PhysicalPlayerListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        PhysicalPlayerManager.getPlayer(e.getPlayer()).onLeave();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        PhysicalPlayerManager.getPlayer(e.getPlayer()).onDeath();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.discoverRecipes(PhysicalRecipes.getRecipes());
        PhysicalPlayerManager.fetchPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        if (PlayerUtils.isSurvival(e.getNewGameMode())) return;
        PhysicalPlayer player = PhysicalPlayerManager.getPlayer(e.getPlayer());
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(player::onChangeGameMode);
    }

}
