package com.voltskiya.mechanics.tribe.command;

import apple.mc.utilities.player.chat.SendMessage;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import org.bukkit.entity.Player;

public interface TribeCommandUtil extends SendMessage {

    default void noTribeMessage(Player player) {
        red(player, "You are not in a tribe!");
    }

    default void onlyOneTribe(Player player, DTribeMember tribePlayer) {
        red(player, "You are already a member of %s".formatted(tribePlayer));
    }
}
