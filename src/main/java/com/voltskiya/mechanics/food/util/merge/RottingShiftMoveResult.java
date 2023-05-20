package com.voltskiya.mechanics.food.util.merge;

public enum RottingShiftMoveResult {
    NO_ACTION,
    COMPLETE,
    PARTIAL;

    public boolean isComplete() {
        return this == COMPLETE;
    }

    public boolean isPartial() {
        return this == PARTIAL;
    }
}
