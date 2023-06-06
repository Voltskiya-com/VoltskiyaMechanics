package com.voltskiya.mechanics.stamina;

public final class StaminaConfig {

    private static StaminaConfig instance;
    public int sprintJumpingIncrement = -115;
    public int jumpingIncrement = -100;
    public int swimmingIncrement = -15;
    public int sprintingIncrement = -15;
    public int crouchingIncrement = 50;
    public int standingStillIncrement = 30;
    public int walkingIncrement = 10;

    public StaminaConfig() {
        instance = this;
    }

    public static StaminaConfig get() {
        return instance;
    }

}
