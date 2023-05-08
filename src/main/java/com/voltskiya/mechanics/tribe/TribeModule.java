package com.voltskiya.mechanics.tribe;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.mechanics.tribe.command.TribeManageCommand;
import com.voltskiya.mechanics.tribe.database.Tribe;

public class TribeModule extends AbstractModule {

    private static TribeModule instance;

    public TribeModule() {
        instance = this;
    }

    public static TribeModule get() {
        return instance;
    }

    @Override
    public void enable() {
        new TribeManageCommand();
        Tribe.loadAll();
    }

    @Override
    public boolean shouldEnable() {
        return false;
    }

    @Override
    public String getName() {
        return "Team";
    }
}
