package com.voltskiya.mechanics.database;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker.Std;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.voltskiya.lib.database.VoltskiyaDatabase;
import com.voltskiya.lib.database.config.VoltskiyaDatabaseConfig;
import com.voltskiya.lib.database.config.VoltskiyaMysqlConfig;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.tribe.entity.DTribe;
import com.voltskiya.mechanics.tribe.entity.DTribeInvite;
import com.voltskiya.mechanics.tribe.entity.claim.DTribeClaim;
import com.voltskiya.mechanics.tribe.entity.claim.DTribeClaimManager;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.query.TribeStorage;
import io.ebean.Database;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MechanicsDatabase extends VoltskiyaDatabase {

    public static final String NAME = "Tribe";
    private static MechanicsDatabase instance;

    public MechanicsDatabase() {
        instance = this;
    }

    public static MechanicsDatabase get() {
        return instance;
    }

    public static Database db() {
        return get().getDB();
    }

    @Override
    protected List<Class<?>> getQueryBeans() {
        return new ArrayList<>(List.of(TribeStorage.class));
    }

    @Override
    protected List<Class<?>> getEntities() {
        List<Class<?>> entities = new ArrayList<>(List.of(BaseEntity.class));
        entities.addAll(List.of(DTribe.class, DTribeMember.class, DTribeInvite.class));
        entities.addAll(List.of(DTribeClaimManager.class, DTribeClaim.class));
        return entities;
    }

    @Override
    protected VoltskiyaDatabaseConfig getConfig() {
        File file = new File(VoltskiyaPlugin.get().getDataFolder(), "DatabaseConfig.json");
        return makeConfig(file, VoltskiyaMysqlConfig.class);
    }

    @Override
    protected Object getObjectMapper() {
        SimpleModule namespacedKeyModule = new SimpleModule();
        return new ObjectMapper()
            .registerModule(namespacedKeyModule)
            .setVisibility(new Std(Visibility.NONE)
                .withFieldVisibility(Visibility.ANY)
                .withGetterVisibility(Visibility.NONE)
                .withSetterVisibility(Visibility.NONE)
                .withCreatorVisibility(Visibility.NONE)
                .withIsGetterVisibility(Visibility.NONE));
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
