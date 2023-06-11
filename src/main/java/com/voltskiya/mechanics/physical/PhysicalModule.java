package com.voltskiya.mechanics.physical;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.mechanics.physical.stamina.StaminaListener;
import com.voltskiya.mechanics.physical.thirst.ThirstCommand;
import com.voltskiya.mechanics.physical.thirst.ThirstListener;
import com.voltskiya.mechanics.physical.thirst.ThirstRecipes;
import java.io.File;

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
        new ThirstListener();
        new ThirstCommand();
        ThirstRecipes.registerRecipes();

        new StaminaListener();

    }

    @Override
    public String getName() {
        return "Physical";
    }

    public File getThirstFile(String children) {
        return this.getFile("Thirst", children);
    }
}
