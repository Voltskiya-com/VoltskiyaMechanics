package com.voltskiya.mechanics.physical.temperature.util.daily;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class VarChangedDaily<T> {

    private final Supplier<T> createNewVar;
    private final Map<UUID, VarChangedDailyValue<T>> valuesPerWorld = new HashMap<>();

    public VarChangedDaily(Supplier<T> createNewDegrees) {
        this.createNewVar = createNewDegrees;
    }

    public T get(@NotNull World world) {
        synchronized (valuesPerWorld) {
            return valuesPerWorld.computeIfAbsent(world.getUID(), uuid -> new VarChangedDailyValue<>())
                .get(world, this.createNewVar);
        }
    }

}