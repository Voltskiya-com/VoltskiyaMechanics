package com.voltskiya.mechanics.stamina;

public class StaminaConfig {//TODO

    private static StaminaConfig instance;

    public static StaminaConfig get() {
        return instance;
    }

    public int getRunAgainThreshold() {
        return 300;
    }

    public int getSprintJumpingIncrement() {
        return -115;
    }

    public int getJumpingIncrement() {
        return -100;
    }

    public int getSwimmingIncrement() {
        return -15;
    }

    public int getSprintingIncrement() {
        return -15;
    }

    public int getCrouchingIncrement() {
        return 50;
    }

    public int getWalkingIncrement() {
        return 10;
    }

    public int getStandingStillIncrement() {
        return 30;
    }
}
