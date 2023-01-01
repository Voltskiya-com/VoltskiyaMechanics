package com.voltskiya.mechanics.stamina;

import lombok.Getter;

@Getter
public final class StaminaConfig {

    private static final StaminaConfig instance = new StaminaConfig();
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

}
