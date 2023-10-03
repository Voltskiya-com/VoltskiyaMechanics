package com.voltskiya.mechanics.physical.temperature.util.daily;

import java.util.Random;

public class DoubleWithFluctuations {

    private transient final Random random = new Random();
    protected double value = 0;
    protected double variancePerc = 5f;
    protected double varianceScaler = 0f;

    /**
     * @param value          the default value
     * @param variancePerc   the percent variance of 'value' in range of 0% to 100%
     * @param varianceScaler additional variance to add to or subtract from the final value
     */
    public DoubleWithFluctuations(double value, double variancePerc, double varianceScaler) {
        this.value = value;
        this.variancePerc = variancePerc;
        this.varianceScaler = varianceScaler;
    }

    public DoubleWithFluctuations() {
    }

    /**
     * @return the generated value: (value) (+/- value * variancePerc) (+/- varianceScaler)
     */
    public double randomVariance() {
        double perc;
        if (-variancePerc >= variancePerc) perc = 0;
        else perc = random.nextDouble(-variancePerc, variancePerc) / 100d;
        double scaler;
        if (-varianceScaler >= varianceScaler) scaler = 0;
        else scaler = random.nextDouble(-varianceScaler, varianceScaler) / 100d;
        return value * (1 + perc) + scaler;
    }


}
