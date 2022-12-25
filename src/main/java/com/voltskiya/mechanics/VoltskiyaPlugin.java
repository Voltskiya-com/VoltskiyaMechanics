package com.voltskiya.mechanics;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.AbstractVoltPlugin;
import com.voltskiya.mechanics.player.VoltskiyaPlayerManager;
import com.voltskiya.mechanics.stamina.StaminaModule;
import com.voltskiya.mechanics.thirst.ThirstModule;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;

public class VoltskiyaPlugin extends AbstractVoltPlugin {

    private static AbstractVoltPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    public static AbstractVoltPlugin get() {
        return instance;
    }

    public void onDisablePost() {
        VoltskiyaPlayerManager.saveNow();
    }

    public void initialize() {
        VoltskiyaPlayerManager.load();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, VoltskiyaPlayerManager::updatePlayers, 0L, 20L);
        // save all online players occasionally
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, VoltskiyaPlayerManager::save, 400L, 400L);
    }

    @Override
    public Collection<AbstractModule> getModules() {
        return List.of(new ThirstModule(), new StaminaModule());
    }
}
