package com.voltskiya.mechanics.physical.temperature.util.daily;

import java.util.function.Supplier;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class VarChangedDailyValue<T> {

    protected T value;
    protected int lastUpdatedDay = -1;

    @NotNull
    public T get(@NotNull World world, Supplier<T> createNewDegrees) {
        boolean isNewDay = VarDailyTimerListener.isNewDay(world, lastUpdatedDay);
        if (isNewDay || value == null) {
            lastUpdatedDay = VarDailyTimerListener.getCurrentDay(world);
            value = createNewDegrees.get();
        }
        return value;
    }
}
