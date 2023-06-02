package com.voltskiya.mechanics.tribe;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.mechanics.tribe.command.TribeCommand;

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
        new TribeCommand();
        new PlayerTeamJoinListener();
    }

    @Override
    public String getName() {
        return "Team";
    }
}
