package com.voltskiya.mechanics;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.AbstractVoltPlugin;
import com.voltskiya.mechanics.chat.ChatModule;
import com.voltskiya.mechanics.database.MechanicsDatabase;
import com.voltskiya.mechanics.physical.PhysicalModule;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerManager;
import com.voltskiya.mechanics.physical.stamina.StaminaModule;
import com.voltskiya.mechanics.tribe.TribeModule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

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
        PhysicalPlayerManager.saveNow();
    }

    @Override
    public void initialize() {
        new MechanicsDatabase();
        PhysicalPlayerManager.load();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, PhysicalPlayerManager::tickPlayers, 0, TICKS_PER_INCREMENT);
        // save all online players occasionally
        Bukkit.getScheduler()
            .runTaskTimer(this, PhysicalPlayerManager::save, TICKS_BETWEEN_SAVE, 20 * TICKS_PER_INCREMENT);
        List<NamespacedKey> removeBossBars = new ArrayList<>();
        Bukkit.getBossBars().forEachRemaining(
            (bar) -> {
                if (bar.getTitle().equals(Display.AIR_BOSS_BAR_KEY))
                    removeBossBars.add(bar.getKey());
            }
        );

        removeBossBars.forEach(Bukkit::removeBossBar);
    }

    @Override
    public Collection<AbstractModule> getModules() {
        return List.of(new PhysicalModule(), new StaminaModule(), new TribeModule(), new ChatModule());
    }
}
