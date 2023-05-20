package com.voltskiya.mechanics.food;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import com.voltskiya.mechanics.food.config.CoolerConfigDatabase;
import com.voltskiya.mechanics.food.config.FoodRotConfig;
import com.voltskiya.mechanics.food.listener.CoolerPlaceListener;
import com.voltskiya.mechanics.food.listener.RenameListener;
import com.voltskiya.mechanics.food.listener.RottingListener;
import com.voltskiya.mechanics.food.listener.RottingSmeltListener;
import com.voltskiya.mechanics.food.service.RottingDecrement;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class FoodDecayModule extends AbstractModule {

    public static World MAIN_WORLD = null;
    private static FoodDecayModule instance;

    public FoodDecayModule() {
        instance = this;
    }

    private static void setMainWorld() {
        World world = Bukkit.getWorld("gmworld");
        if (world == null) MAIN_WORLD = Bukkit.getWorlds().get(0);
        else MAIN_WORLD = world;
    }

    public static FoodDecayModule get() {
        return instance;
    }

    @Override
    public void enable() {
        setMainWorld();
        CoolerConfigDatabase.init();
        RottingDecrement.start();
        new RottingListener();
        new RenameListener();
        new RottingSmeltListener();
        new CoolerPlaceListener();
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(configJson(CoolerConfigDatabase.class, "Cooler.config"),
            configJson(FoodRotConfig.class, "FoodRot.config"));
    }

    @Override
    public String getName() {
        return "FoodDecay";
    }
}
