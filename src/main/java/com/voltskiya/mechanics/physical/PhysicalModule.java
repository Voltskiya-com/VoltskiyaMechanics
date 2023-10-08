package com.voltskiya.mechanics.physical;

import static com.voltskiya.mechanics.physical.player.ActionBarDisplay.AIR_BOSS_BAR_KEY;
import static com.voltskiya.mechanics.physical.player.ActionBarDisplay.AIR_BOSS_BAR_TITLE;
import static com.voltskiya.mechanics.physical.player.ActionBarDisplay.TEMPERATURE_BOSS_BAR_KEY;

import apple.mc.utilities.data.serialize.GsonSerializeMC;
import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerListener;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerManager;
import com.voltskiya.mechanics.physical.stamina.StaminaConfig;
import com.voltskiya.mechanics.physical.stamina.StaminaListener;
import com.voltskiya.mechanics.physical.temperature.config.biome.TemperatureBiomeDB;
import com.voltskiya.mechanics.physical.temperature.config.blocks.TemperatureBlocksConfig;
import com.voltskiya.mechanics.physical.temperature.config.clothing.ClothingConfig;
import com.voltskiya.mechanics.physical.temperature.config.effect.TemperatureEffectConfig;
import com.voltskiya.mechanics.physical.temperature.util.daily.VarDailyTimerListener;
import com.voltskiya.mechanics.physical.thirst.ThirstCommand;
import com.voltskiya.mechanics.physical.thirst.ThirstListener;
import com.voltskiya.mechanics.physical.thirst.config.ThirstConfig;
import com.voltskiya.mechanics.physical.thirst.item.ThirstRecipes;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.KeyedBossBar;

public class PhysicalModule extends AbstractModule {


    public static final int TICKS_PER_INCREMENT = 5;
    private static final int TICKS_BETWEEN_SAVE = 20 * 30;
    private static PhysicalModule instance;

    public PhysicalModule() {
        instance = this;
    }

    public static PhysicalModule get() {
        return instance;
    }

    private static void removeAirBar() {
        List<NamespacedKey> removeBossBars = new ArrayList<>();
        Bukkit.getBossBars().forEachRemaining((bar) -> {
            if (shouldRemoveBossBar(bar)) removeBossBars.add(bar.getKey());
        });
        removeBossBars.forEach(Bukkit::removeBossBar);
    }

    private static boolean shouldRemoveBossBar(KeyedBossBar bar) {
        String key = bar.getKey().getKey();
        String title = bar.getTitle();
        return title.equals(AIR_BOSS_BAR_TITLE) ||
            key.startsWith(AIR_BOSS_BAR_KEY) ||
            key.startsWith(TEMPERATURE_BOSS_BAR_KEY);
    }

    @Override
    public void onDisable() {
        PhysicalPlayerManager.onDisable();
        PhysicalPlayerManager.saveNow();
    }

    @Override
    public void enable() {
        enableTasks();

        ThirstRecipes.registerRecipes();
        new ThirstListener();
        new ThirstCommand();

        new StaminaListener();

        new PhysicalPlayerListener();

        new VarDailyTimerListener();
    }

    public void enableTasks() {
        PhysicalPlayerManager.load();
        Bukkit.getScheduler()
            .runTaskTimer(VoltskiyaPlugin.get(), PhysicalPlayerManager::tickPlayers, 0, TICKS_PER_INCREMENT);
        // save all online players occasionally
        Bukkit.getScheduler()
            .runTaskTimer(VoltskiyaPlugin.get(), PhysicalPlayerManager::save, TICKS_BETWEEN_SAVE, TICKS_BETWEEN_SAVE);
        removeAirBar();
    }

    @Override
    public String getName() {
        return "Physical";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(configJson(ThirstConfig.class, "ThirstConfig"),
            configJson(StaminaConfig.class, "StaminaConfig"),
            configFolder("Temperature",
                configJson(TemperatureBlocksConfig.class, "TemperatureBlocks"),
                configJson(ClothingConfig.class, "Clothing"),
                configJson(TemperatureEffectConfig.class, "Effects"),
                configJson(TemperatureBiomeDB.class, "TemperatureBiomes")
                    .asJson(GsonSerializeMC.completeGsonBuilderMC().create())
            )
        );
    }
}
