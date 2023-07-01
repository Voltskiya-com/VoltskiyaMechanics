package com.voltskiya.mechanics.physical;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.configs.factory.AppleConfigLike;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerListener;
import com.voltskiya.mechanics.physical.stamina.StaminaListener;
import com.voltskiya.mechanics.physical.thirst.ThirstCommand;
import com.voltskiya.mechanics.physical.thirst.ThirstListener;
import com.voltskiya.mechanics.physical.thirst.config.ThirstConfig;
import com.voltskiya.mechanics.physical.thirst.item.ThirstRecipes;
import java.io.File;
import java.util.List;

public class PhysicalModule extends AbstractModule {

    private static PhysicalModule instance;

    public PhysicalModule() {
        instance = this;
    }

    public static PhysicalModule get() {
        return instance;
    }

    @Override
    public void enable() {
        ThirstRecipes.registerRecipes();
        new ThirstListener();
        new ThirstCommand();

        new StaminaListener();

        new PhysicalPlayerListener();
    }

    @Override
    public String getName() {
        return "Physical";
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(configJson(ThirstConfig.class, "ThirstConfig"));
    }

    public File getThirstFile(String children) {
        return this.getFile("Thirst", children);
    }
}
