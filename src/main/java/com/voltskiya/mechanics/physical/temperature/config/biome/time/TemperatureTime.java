package com.voltskiya.mechanics.physical.temperature.config.biome.time;

public enum TemperatureTime {
    MORNING(6000), // right when the sun comes up
    DAY(10500), // daytime
    EVENING(17000), // right before sunfall
    NIGHT(21500); // night
    public static final int DAILY_TIME = 24000;
    private static TemperatureTime[] times;

    static {
        getOrderedTimes();
    }

    final int earliest;

    private int index;

    TemperatureTime(int earliest) {
        this.earliest = earliest;
    }

    public static MergedTemperatureTime getTime(long time) {
        time += DAILY_TIME + 6000;
        time %= DAILY_TIME;

        if (time < MORNING.earliest || time >= NIGHT.earliest) {
            return new MergedTemperatureTime(EVENING, NIGHT, MORNING, time);
        } else if (time < DAY.earliest) {
            return new MergedTemperatureTime(NIGHT, MORNING, DAY, time);
        } else if (time < EVENING.earliest) {
            return new MergedTemperatureTime(MORNING, DAY, EVENING, time);
        } else {
            return new MergedTemperatureTime(DAY, EVENING, NIGHT, time);
        }
    }

    private static TemperatureTime[] getOrderedTimes() {
        if (times != null)
            return times;
        times = values();
        for (int i = 0; i < times.length; i++) {
            times[i].index = i;
        }
        return times;
    }

    public TemperatureTime next() {
        int timeLength = getOrderedTimes().length;
        int nextIndex = (index + 1) % timeLength;
        return getOrderedTimes()[nextIndex];
    }

    public long timeUntil(long time) {
        // add a day to verify positive
        return (this.earliest - time + DAILY_TIME) % DAILY_TIME;
    }
}