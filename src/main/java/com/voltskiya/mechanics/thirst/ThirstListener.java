package com.voltskiya.mechanics.thirst;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class ThirstListener implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        ThirstyPlayer.consume(e.getPlayer(), e.getItem());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ThirstyPlayer.join(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        ThirstyPlayer.leave(e.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        ThirstyPlayer.reset(e.getPlayer());
    }

    @EventHandler
    public void onSrint(PlayerToggleSprintEvent e) {
        ThirstyPlayer.getPlayer(e.getPlayer()).onSprint();
    }
}
