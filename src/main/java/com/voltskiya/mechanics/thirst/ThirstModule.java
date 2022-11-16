package com.voltskiya.mechanics.thirst;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.BukkitCommandManager;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import org.bukkit.Bukkit;

public class ThirstModule extends AbstractModule {

    private static BukkitCommandManager acf;

    public static void registerCommand(BaseCommand command) {
        acf.registerCommand(command);
    }


    @Override
    public void enable() {
        VoltskiyaPlugin plugin = VoltskiyaPlugin.get();
        ThirstyPlayer.load();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, ThirstyPlayer::updatePlayers, 0, 20);
        Bukkit.getPluginManager().registerEvents(new ThirstListener(), plugin);
        acf = new BukkitCommandManager(plugin);
        new ThirstCommandACF();
    }

    @Override
    public String getName() {
        return "Thirst";
    }


    @Override
    public void onDisable() {
        ThirstyPlayer.save();
    }
}
