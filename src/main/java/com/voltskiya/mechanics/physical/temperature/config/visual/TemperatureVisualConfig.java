package com.voltskiya.mechanics.physical.temperature.config.visual;

public class TemperatureVisualConfig {

    private static TemperatureVisualConfig instance;
    public WindVisualConfig wind = new WindVisualConfig();

    public TemperatureVisualConfig() {
        instance = this;
    }

    public static TemperatureVisualConfig get() {
        return instance;
    }
}
