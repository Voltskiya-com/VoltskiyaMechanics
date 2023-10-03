package com.voltskiya.mechanics.physical.temperature.config.biome.time;

import static com.voltskiya.mechanics.physical.temperature.config.biome.time.TemperatureTime.DAILY_TIME;

public class MergedTemperatureTime {

    private final double percNow;
    private final double percLate;
    private final double percEarly;

    private final TemperatureTime earlyTime;
    private final TemperatureTime nowTime;
    private final TemperatureTime lateTime;

    public MergedTemperatureTime(TemperatureTime earlyTime, TemperatureTime nowTime, TemperatureTime lateTime, long realTime) {
        this.earlyTime = earlyTime;
        this.nowTime = nowTime;
        this.lateTime = lateTime;

        int range = lateTime.earliest - nowTime.earliest + DAILY_TIME;
        range %= 240000;

        double percEarly = realTime - nowTime.earliest;
        if (percEarly < 0) percEarly += DAILY_TIME;
        this.percEarly = (1 - (percEarly / range)) / 3;

        double percLate = lateTime.earliest - realTime;
        if (percLate < 0) percLate += DAILY_TIME;
        this.percLate = (1 - (percLate / range)) / 3;

        percNow = 1 - (this.percEarly + this.percLate);
    }

    public TemperatureTime getEarly() {
        return this.earlyTime;
    }

    public TemperatureTime getNow() {
        return this.nowTime;
    }

    public TemperatureTime getLate() {
        return this.lateTime;
    }

    public double getPercEarly() {
        return this.percEarly;
    }

    public double getPercNow() {
        return percNow;
    }

    public double getPercLate() {
        return percLate;
    }
}
