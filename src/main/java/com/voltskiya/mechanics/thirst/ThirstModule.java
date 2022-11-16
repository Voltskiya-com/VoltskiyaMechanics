package com.voltskiya.mechanics.thirst;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.BukkitCommandManager;
import org.bukkit.Bukkit;

public class ThirstModule extends AbstractModule {

    private static BukkitCommandManager acf;

    public static void registerCommand(BaseCommand command) {
        acf.registerCommand(command);
    }

    public static ThirstModule instance;

    public static ThirstModule get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        ThirstyPlayer.load();
        Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), ThirstyPlayer::updatePlayers, 0, 20);
        getPlugin().registerEvents(new ThirstListener());
        acf = new BukkitCommandManager(getPlugin());
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
