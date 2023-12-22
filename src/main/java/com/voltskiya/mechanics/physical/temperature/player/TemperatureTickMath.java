package com.voltskiya.mechanics.physical.temperature.player;

public class TemperatureTickMath {

    private static double overshootDirection(double diff, double minOrMax) {
        // magic number just meant to overshoot towards finalGoal
        double overshootDistance = 0.10;
        double overshoot = Math.copySign(overshootDistance * minOrMax, diff);
        return diff + overshoot;
    }

    public static double doTickMath(double rate, double current, double goal, double range) {
        if (rate == 0) return current;
        boolean isPos = rate > 0;

        double overshotDirection = overshootDirection(goal - current, range);
        double direction = Math.copySign(overshotDirection * rate, isPos ? 1 : -1);

        boolean isGoalNearby = Math.abs(goal - current) < Math.abs(direction);
        return isGoalNearby ? goal - current : direction;
    }
}
