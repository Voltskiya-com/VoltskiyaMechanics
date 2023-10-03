package com.voltskiya.mechanics.physical.temperature.check;

public class TemperatureConsts {

    private static TemperatureConsts instance = new TemperatureConsts();
    public double maxWindSpeed = 250;
    public BlockSourcesConsts blockSources = new BlockSourcesConsts();
    public WetnessConsts wetness = new WetnessConsts();
    public HeatConsts temperature = new HeatConsts();

    public TemperatureConsts() {
        instance = this;
    }

    public static TemperatureConsts get() {
        return instance;
    }

    public static class BlockSourcesConsts {

        // regardless of insideness, blockSources has an impact
        public double rawPercImportance = 1;

        // with an insideness of 1, blockSources is multiplied by an additional insidessMultiplier
        public double insidenessMultiplier = 2;

        // believed to impact performance O(n) = n^3
        public int checkRadius = 40;
        // number between 1 and 2 which determines how to reduce distance sources impact on temperature
        public double distanceImpact = 1 + 1 / 7d;
    }

    public static class WetnessConsts {

        public double maxWetness = 100;
        public double baseSoakPerSec = 8d / 100; // 1/5 of 100 wetness per second
        public double baseDryingPerSec = 4d / 100; // 1/5 of 100 wetness per second
        public double heatImpactOnDrying = 1.0;
        public double windImpactOnDrying = 2.0;
        protected double minImpactOnWinterClothing = 0.10; // range: [0, 1]; realistically below 0.25
        protected double maxImpactOnWinterClothing = 0.70; // range: [0, 1]; realistically below 0.25

        /**
         * if the wind is close to 1, then use close to maxImpact because wind + wet means you'll get cold fast in winter conditions
         * <p/>
         * if the wind is close to 0, then use close to minImpact because noWind + wet won't make you cold as fast as if there was
         * wind
         *
         * @param windImpact the current windImpact between 0 and 1
         * @return the impact that wetness has on winter clothing
         */
        public double impactOnWinterClothing(double windImpact) {
            double range = maxImpactOnWinterClothing - minImpactOnWinterClothing;
            return windImpact * range + minImpactOnWinterClothing;
        }
    }

    public static class HeatConsts {

        public double effectiveMaxAirTemp = 200;
        public double baseHeatTransferPerSec = 1d / 5 / 200; // 1/5 of effectiveMaxAirTemp per second
        public double evaporationImpactOnHeatRate = .25;
    }
}
