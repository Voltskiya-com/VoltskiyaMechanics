package com.voltskiya.mechanics;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.AbstractVoltPlugin;
import com.voltskiya.mechanics.food.FoodDecayModule;
import com.voltskiya.mechanics.player.VoltskiyaPlayerManager;
import com.voltskiya.mechanics.stamina.StaminaModule;
import com.voltskiya.mechanics.thirst.ThirstModule;
import com.voltskiya.mechanics.tribe.TribeModule;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;

public class VoltskiyaPlugin extends AbstractVoltPlugin {

    public static final int TICKS_BETWEEN_SAVE = 20 * 30;
    private static final int TICKS_PER_INCREMENT = 1;
    private static AbstractVoltPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    public static AbstractVoltPlugin get() {
        return instance;
    }

    @Override
    public void onDisablePost() {
        VoltskiyaPlayerManager.saveNow();
    }

    @Override
    public void initialize() {
        VoltskiyaPlayerManager.load();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, VoltskiyaPlayerManager::tickPlayers, 0, TICKS_PER_INCREMENT);
        // save all online players occasionally
        Bukkit.getScheduler()
            .runTaskTimer(this, VoltskiyaPlayerManager::save, TICKS_BETWEEN_SAVE, 20 * TICKS_PER_INCREMENT);

        Bukkit.getPluginManager().registerEvents(new VoltskiyaListener(), this);
    }

    @Override
    public Collection<AbstractModule> getModules() {
        return List.of(new ThirstModule(), new FoodDecayModule(), new StaminaModule(), new TribeModule());
    }
}
