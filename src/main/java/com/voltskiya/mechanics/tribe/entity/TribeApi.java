package com.voltskiya.mechanics.tribe.entity;

import com.voltskiya.mechanics.tribe.entity.claim.DTribeClaimManager;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.entity.member.TribeRole;
import com.voltskiya.mechanics.tribe.entity.member.query.QDTribeMember;
import com.voltskiya.mechanics.tribe.entity.query.QDTribe;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;

public class TribeApi {

    private static final Pattern TRIBE_NAME_REGEX = Pattern.compile("^.{3,40}$");
    private static final Pattern TRIBE_TAG_REGEX = Pattern.compile("^[a-zA-Z]{3,5}$");

    public static DTribe createAndRegister(String name, Player leaderPlayer, String tag) {
        if (findTribe(name) != null) {
            throw new IllegalArgumentException("There is already a tribe named %s".formatted(name));
        }
        if (findTribeByTag(tag) != null) {
            throw new IllegalArgumentException("There is already a tribe with tag %s".formatted(tag));
        }
        DTribe tribe = new DTribe(name, tag);
        DTribeClaimManager claimManager = new DTribeClaimManager(tribe);
        tribe.claims = claimManager;
        claimManager.save();
        if (!TRIBE_NAME_REGEX.asMatchPredicate().test(name))
            throw new IllegalArgumentException("%s must be between 3 and 40 characters long");
        if (!TRIBE_TAG_REGEX.asMatchPredicate().test(tag))
            throw new IllegalArgumentException("%s must be contain only alphabetic characters and be between 3 and 5 characters long");
        DTribeMember leader = tribe.join(leaderPlayer);
        tribe.save();
        leader.setRole(TribeRole.LEADER);
        leader.save();
        tribe.getTeam().addPlayer(leaderPlayer);
        return tribe;
    }

    public static DTribeMember findPlayer(UUID player) {
        return new QDTribeMember().where().player.eq(player).findOne();
    }

    public static DTribe findTribe(String name) {
        return new QDTribe().where().name.ieq(name).findOne();
    }

    public static DTribe findTribeByTag(String tag) {
        return new QDTribe().where().tag.ieq(tag).findOne();
    }
}
