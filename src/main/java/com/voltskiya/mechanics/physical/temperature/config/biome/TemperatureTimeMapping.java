package com.voltskiya.mechanics.physical.temperature.config.biome;

import com.voltskiya.mechanics.physical.temperature.config.biome.time.MergedTemperatureTime;
import com.voltskiya.mechanics.physical.temperature.config.biome.time.TemperatureTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TemperatureTimeMapping<T> {

    protected T fallback;
    protected Map<TemperatureTime, T> timeMap = new HashMap<>();

    public TemperatureTimeMapping(T fallback) {
        this.fallback = fallback;
    }

    public TemperatureTimeMapping() {
    }

    public <R> R get(MergedTemperatureTime time, BiFunction<Double, T, R> map, MergeTemperatureTime<R> merge) {
        R early = this.get(time.getEarly(), time.getPercEarly(), map);
        R now = this.get(time.getNow(), time.getPercNow(), map);
        R late = this.get(time.getLate(), time.getPercLate(), map);
        return merge.merge(early, now, late);
    }

    private <R> R get(TemperatureTime time, double perc, BiFunction<Double, T, R> map) {
        T val = this.timeMap.getOrDefault(time, this.fallback);
        return map.apply(perc, val);
    }

    public void toEach(Consumer<T> fn) {
        fn.accept(this.fallback);
        this.timeMap.values().forEach(fn);
    }

    @FunctionalInterface
    public interface MergeTemperatureTime<R> {

        R merge(R early, R now, R late);
    }
}
