package com.voltskiya.mechanics.stamina;

public class StaminaConfig {

    private static StaminaConfig instance;
    public int runAgainThreshold = 300;
    public int sprintJumpingIncrement = -115;
    public int jumpingIncrement = -100;
    public int swimmingIncrement = -15;
    public int sprintingIncrement = -15;
    public int crouchingIncrement = 50;
    public int standingStillIncrement = 30;
    public int walkingIncrement = 10;

    public static StaminaConfig get() {
        return instance;
    }

    public StaminaConfig() {
        instance = this;
    }

    public int getRunAgainThreshold() {
        return runAgainThreshold;
    }

    public int getSprintJumpingIncrement() {
        return sprintJumpingIncrement;
    }

    public int getJumpingIncrement() {
        return jumpingIncrement;
    }

    public int getSwimmingIncrement() {
        return swimmingIncrement;
    }

    public int getSprintingIncrement() {
        return sprintingIncrement;
    }

    public int getCrouchingIncrement() {
        return crouchingIncrement;
    }

    public int getWalkingIncrement() {
        return walkingIncrement;
    }

    public int getStandingStillIncrement() {
        return standingStillIncrement;
    }
}
