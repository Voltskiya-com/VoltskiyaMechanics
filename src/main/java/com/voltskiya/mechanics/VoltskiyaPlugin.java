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
    private static final int TICKS_PER_SECOND = 20;

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
        Bukkit.getScheduler().runTaskTimer(this, VoltskiyaPlayerManager::tickPlayers, 0, TICKS_PER_SECOND);
        // save all online players occasionally
        Bukkit.getScheduler().runTaskTimer(this, VoltskiyaPlayerManager::save, 20 * TICKS_PER_SECOND, 20 * TICKS_PER_SECOND);

        Bukkit.getPluginManager().registerEvents(new VoltskiyaListener(), this);
    }

    @Override
    public Collection<AbstractModule> getModules() {
        return List.of(new ThirstModule(), new StaminaModule());
    }
}
