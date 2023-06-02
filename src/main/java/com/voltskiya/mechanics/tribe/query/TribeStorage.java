package com.voltskiya.mechanics.tribe.query;

import com.voltskiya.mechanics.tribe.entity.DTribe;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.entity.member.query.QDTribeMember;
import com.voltskiya.mechanics.tribe.entity.query.QDTribe;
import java.util.UUID;

public class TribeStorage {

    public static DTribeMember findPlayer(UUID player) {
        return new QDTribeMember().where().player.eq(player).findOne();
    }

    public static DTribe findTribe(String name) {
        return new QDTribe().where().name.ieq(name).findOne();
    }

}
