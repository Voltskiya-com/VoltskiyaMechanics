package com.voltskiya.mechanics.physical.temperature.util.daily;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.jetbrains.annotations.NotNull;

public class VarDailyTimerListener implements Listener {

    private static final Map<UUID, VarDailyTimer> dayTracker = new HashMap<>();

    public VarDailyTimerListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    public static boolean isNewDay(@NotNull World world, int lastUpdatedDay) {
        synchronized (dayTracker) {
            VarDailyTimer day = dayTracker.get(world.getUID());
            if (day == null) return true;
            if (day.isNextDay(world))
                day = updateDay(world);
            return day.currentDay != lastUpdatedDay;
        }
    }

    public static int getCurrentDay(@NotNull World world) {
        VarDailyTimer day = dayTracker.get(world.getUID());
        if (day == null || day.isNextDay(world)) return updateDay(world).currentDay;
        return day.currentDay;
    }

    private synchronized static VarDailyTimer updateDay(World world) {
        VarDailyTimer previousDay = dayTracker.get(world.getUID());
        int currentDay = previousDay == null ? 1 : previousDay.currentDay;

        long timeToEndDay = (24000 - world.getTime()) + world.getFullTime();

        VarDailyTimer day = new VarDailyTimer(timeToEndDay, currentDay);
        dayTracker.put(world.getUID(), day);
        return day;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDayChange(TimeSkipEvent event) {
        World world = event.getWorld();
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> updateDay(world));
    }

    private record VarDailyTimer(long nextDayAt, int currentDay) {

        public boolean isNextDay(World world) {
            return world.getFullTime() >= nextDayAt;
        }
    }
}
