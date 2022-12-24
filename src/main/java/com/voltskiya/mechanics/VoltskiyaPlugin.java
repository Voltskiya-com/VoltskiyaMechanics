package com.voltskiya.mechanics;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.AbstractVoltPlugin;
import com.voltskiya.mechanics.stamina.StaminaModule;
import com.voltskiya.mechanics.thirst.ThirstModule;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.List;

public class VoltskiyaPlugin extends AbstractVoltPlugin {

    private static AbstractVoltPlugin instance;

    public VoltskiyaPlugin() {
        instance = this;
    }

    public static AbstractVoltPlugin get() {
        return instance;
    }

    public void onDisablePost() {
        VoltskiyaPlayer.save();
    }

    public void initialize() {
        VoltskiyaPlayer.load();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, VoltskiyaPlayer::updatePlayers, 0L, 20L);
    }

    @Override
    public Collection<AbstractModule> getModules() {
        return List.of(new ThirstModule(), new StaminaModule());
    }
}
