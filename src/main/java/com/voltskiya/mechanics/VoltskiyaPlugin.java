package com.voltskiya.mechanics;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.AbstractVoltPlugin;
import com.voltskiya.mechanics.chat.ChatModule;
import com.voltskiya.mechanics.database.MechanicsDatabase;
import com.voltskiya.mechanics.physical.PhysicalModule;
import com.voltskiya.mechanics.tribe.TribeModule;
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

    @Override
    public void initialize() {
        new MechanicsDatabase();
    }

    @Override
    public Collection<AbstractModule> getModules() {
        return List.of(new PhysicalModule(), new TribeModule(), new ChatModule());
    }
}
