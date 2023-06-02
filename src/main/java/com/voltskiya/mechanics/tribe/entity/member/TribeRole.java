package com.voltskiya.mechanics.tribe.entity.member;

import apple.utilities.util.Pretty;
import io.ebean.annotation.DbEnumValue;
import net.kyori.adventure.text.format.TextColor;

public enum TribeRole {
    LEADER(0),
    LIEUTENANT(1),
    MEMBER(2);

    private final int rank;

    TribeRole(int rank) {
        this.rank = rank;
    }

    public boolean canInvite() {
        return this != MEMBER;
    }

    public boolean canKick(TribeRole otherRole) {
        return getRank() < otherRole.getRank();
    }

    @DbEnumValue
    public String getDBValue() {
        return this.name();
    }

    public boolean isLeader() {
        return this == LEADER;
    }

    public int getRank() {
        return this.rank;
    }

    public String displayName() {
        return Pretty.spaceEnumWords(this.name());
    }

    public TextColor getColor() {
        int color = switch (this) {
            case LEADER -> 0xff3d3d;
            case LIEUTENANT -> 0x5d2bff;
            case MEMBER -> 0x27dbf2;
        };
        return TextColor.color(color);
    }


    @Override
    public String toString() {
        return this.displayName();
    }
}
