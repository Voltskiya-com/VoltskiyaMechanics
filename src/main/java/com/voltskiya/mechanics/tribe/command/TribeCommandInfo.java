package com.voltskiya.mechanics.tribe.command;

import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.entity.member.TribeRole;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

public interface TribeCommandInfo extends TribeCommandUtil {

    default void list(Player player, DTribeMember tribePlayer) {
        if (tribePlayer == null) {
            red(player, "You are not in a tribe!");
            return;
        }
        List<Component> message = new ArrayList<>();
        List<DTribeMember> members = tribePlayer.getTribe().getMembers();
        for (DTribeMember member : members) {
            TribeRole role = member.getRole();
            String rank = role.displayName();
            TextColor color = role.getColor();
            String playerName = member.getPlayer().getName();
            String joined = DateTimeFormatter.ofPattern("dd'd/'mm'm/'uuuu'y'").format(member.getJoined().atOffset(ZoneOffset.UTC));
            message.add(Component.text("[%s] %s - joined at %s".formatted(rank, playerName, joined), color));
        }
        player.sendMessage(Component.join(JoinConfiguration.newlines(), message));
    }

}
