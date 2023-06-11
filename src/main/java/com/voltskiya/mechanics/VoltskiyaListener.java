package com.voltskiya.mechanics;

import com.voltskiya.mechanics.player.VoltskiyaPlayerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VoltskiyaListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        VoltskiyaPlayerManager.getPlayer(e.getPlayer()).onLeave();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        VoltskiyaPlayerManager.getPlayer(e.getPlayer()).onDeath();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.discoverRecipes(VoltskiyaRecipeManager.getRecipes());
        VoltskiyaPlayerManager.getPlayer(player);
    }

    @EventHandler
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        if (GameMode.ADVENTURE != e.getNewGameMode() && GameMode.SURVIVAL != e.getNewGameMode())
            e.getPlayer().sendActionBar(Component.empty()); //Disables the display when in creative
    }

}
