package com.voltskiya.mechanics.stamina;

import com.voltskiya.lib.AbstractModule;

public class StaminaModule  extends AbstractModule {
    @Override
    public void enable() {
        getPlugin().registerEvents(new StaminaListener());
    }

    @Override
    public String getName() {
        return "Stamina";
    }
}
