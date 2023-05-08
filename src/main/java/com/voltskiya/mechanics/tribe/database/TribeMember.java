package com.voltskiya.mechanics.tribe.database;

import java.util.UUID;

public class TribeMember {

    public TribeRole role;
    public UUID player;

    public TribeMember(UUID player) {
        this.role = TribeRole.MEMBER;
        this.player = player;
    }

    public TribeMember() {
    }
}
