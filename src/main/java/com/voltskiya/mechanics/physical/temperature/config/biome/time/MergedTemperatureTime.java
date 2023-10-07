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

        int range = lateTime.earliest - earlyTime.earliest + DAILY_TIME;
        range %= 240000;

        double percEarly = realTime - nowTime.earliest;
        if (percEarly < 0) percEarly += DAILY_TIME;
        percEarly = Math.max(0, (percEarly / range) - .2);

        double percLate = lateTime.earliest - realTime;
        if (percLate < 0) percLate += DAILY_TIME;
        percLate = Math.max(0, (percLate / range) - .2);

        this.percEarly = percEarly;
        this.percLate = percLate;
        this.percNow = 1 - this.percEarly - this.percLate;
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

    @Override
    public String toString() {
        return "MergedTemperatureTime{" +
            "percNow=" + percNow +
            ", percLate=" + percLate +
            ", percEarly=" + percEarly +
            ", earlyTime=" + earlyTime +
            ", nowTime=" + nowTime +
            ", lateTime=" + lateTime +
            '}';
    }
}
