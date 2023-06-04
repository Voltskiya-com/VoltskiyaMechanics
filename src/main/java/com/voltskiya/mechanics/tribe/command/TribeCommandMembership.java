package com.voltskiya.mechanics.tribe.command;

import com.voltskiya.mechanics.tribe.entity.DTribe;
import com.voltskiya.mechanics.tribe.entity.DTribeInvite;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.query.TribeStorage;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface TribeCommandMembership extends TribeCommandUtil {

    default void create(Player player, DTribeMember tribePlayer, String name, String tag) {
        if (tribePlayer != null) {
            onlyOneTribe(player, tribePlayer);
            return;
        }
        try {
            DTribe tribe = DTribe.createAndRegister(name, player, tag);
            tribe.showWelcome(player);
        } catch (IllegalArgumentException e) {
            red(player, e.getMessage());
        }
    }


    default void leave(Player player, DTribeMember member, String confirm) {
        if (member == null) {
            noTribeMessage(player);
            return;
        }
        String tribeName = member.getTribe().getName();
        if (confirm == null || !confirm.equals("confirm")) {
            red(player, "Are you sure you want to leave %s? To confirm, run /tribe membership leave confirm", tribeName);
            return;
        }
        member.leave();
        aqua(player, "You have left %s".formatted(tribeName));
    }

    default void invite(Player player, DTribeMember tribePlayer, OfflinePlayer joining) {
        if (tribePlayer == null) {
            noTribeMessage(player);
            return;
        }
        if (!tribePlayer.getRole().canInvite()) {
            red(player, "You do not have permission to invite others!");
            return;
        }
        DTribe tribe = tribePlayer.getTribe();
        UUID joiningUUID = joining.getUniqueId();
        if (tribe.getInvite(joiningUUID) != null) {
            red(player, "There is already an invite sent to %s. Though they received another invite".formatted(joining.getName()));
            inviteMessage(joining, tribe);
            return;
        }
        DTribeInvite invite = new DTribeInvite(player.getUniqueId(), joiningUUID, tribe);
        invite.save();
        aqua(player, "Invite has been sent to " + joining.getName());
        inviteMessage(joining, tribe);
    }


    default void kick(Player player, DTribeMember tribePlayer, OfflinePlayer member) {
        if (tribePlayer == null) {
            noTribeMessage(player);
            return;
        }
        DTribe tribe = tribePlayer.getTribe();
        DTribeMember otherMember = tribe.getMember(member.getUniqueId());
        if (otherMember == null) {
            red(player, member.getName() + " is not in your tribe!");
            return;
        }
        if (!tribePlayer.getRole().canKick(otherMember.getRole())) {
            red(player, "You do not have permission to kick a %s".formatted(otherMember.getRole()));
            return;
        }
        otherMember.leave();

        String otherPlayerName = otherMember.getPlayer().getName();
        tribe.announce(Component.text("%s has kicked %s from the tribe!".formatted(player.getName(), otherPlayerName)));
        Player otherPlayer = otherMember.getPlayer().getPlayer();
        if (otherPlayer != null)
            otherPlayer.sendMessage(Component.text("You have been kicked from %s!".formatted(tribe.getName())));
    }

    default void join(Player player, DTribeMember tribePlayer, String name) {
        if (tribePlayer != null) {
            onlyOneTribe(player, tribePlayer);
            return;
        }
        DTribe inviterTribe = TribeStorage.findTribe(name);
        if (inviterTribe == null) {
            red(player, "There is no guild named %s");
            return;
        }
        DTribeInvite invite = inviterTribe.getInvite(player.getUniqueId());
        if (invite == null) {
            red(player, "You do not have an invite to join %s".formatted(inviterTribe.getName()));
            return;
        }
        inviterTribe.join(player).save();
        invite.delete();
        inviterTribe.showWelcome(player);
    }

    private void inviteMessage(OfflinePlayer joining, DTribe tribe) {
        if (joining.getPlayer() == null) return;
        Component inviteMessage = Component.text("You have been invited to join '%s'".formatted(tribe.getName()))
            .appendNewline()
            .append(Component.text("Click here to join!", TextColor.color(0x03befc))
                .clickEvent(ClickEvent.clickEvent(Action.RUN_COMMAND, "/tribe membership join " + tribe.getName())));
        joining.getPlayer().sendMessage(inviteMessage);
    }

}
