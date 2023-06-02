package com.voltskiya.mechanics.tribe.command;

import apple.mc.utilities.player.chat.SendMessage;
import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.annotation.CommandAlias;
import com.voltskiya.lib.acf.annotation.CommandCompletion;
import com.voltskiya.lib.acf.annotation.CommandPermission;
import com.voltskiya.lib.acf.annotation.Name;
import com.voltskiya.lib.acf.annotation.Optional;
import com.voltskiya.lib.acf.annotation.Subcommand;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.tribe.entity.DTribe;
import com.voltskiya.mechanics.tribe.entity.DTribeInvite;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.entity.member.TribeRole;
import com.voltskiya.mechanics.tribe.query.TribeStorage;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("tribe")
@CommandPermission("volt.tribe")
public class TribeCommand extends BaseCommand implements SendMessage {

    public TribeCommand() {
        VoltskiyaPlugin.get().getCommandManager().getCommandContexts()
            .registerIssuerAwareContext(DTribeMember.class, ctx -> TribeStorage.findPlayer(ctx.getPlayer().getUniqueId()));
        VoltskiyaPlugin.get().registerCommand(this);
    }

    @Subcommand("create")
    public void create(Player player, DTribeMember tribePlayer, String name) {
        if (tribePlayer != null) {
            red(player, "You are already a member of the tribe: %s".formatted(tribePlayer));
            return;
        }
        try {
            DTribe tribe = DTribe.createAndRegister(name, player);
            tribe.showWelcome(player);
        } catch (IllegalArgumentException e) {
            red(player, e.getMessage());
        }
    }

    @Subcommand("leave")
    @CommandCompletion("@nothing")
    public void leave(Player player, DTribeMember member, @Optional @Name("confirm") String confirm) {
        if (member == null) {
            red(player, "You are not in a tribe!");
            return;
        }
        String tribeName = member.getTribe().getName();
        if (confirm == null || !confirm.equals("confirm")) {
            red(player, "Are you sure you want to leave %s? To confirm, run /tribe leave confirm", tribeName);
            return;
        }
        member.leave();
        aqua(player, "You have left %s".formatted(tribeName));
    }

    @Subcommand("list")
    public void list(Player player, DTribeMember tribePlayer) {
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

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void invite(Player player, DTribeMember tribePlayer, OfflinePlayer joining) {
        if (tribePlayer == null) {
            red(player, "You are not in a tribe!");
            return;
        }
        if (!tribePlayer.getRole().canInvite()) {
            red(player, "You do not have permission to invite others!");
            return;
        }
        DTribe tribe = tribePlayer.getTribe();
        UUID joiningUUID = joining.getUniqueId();
        if (tribe.getInvite(joiningUUID) != null) {
            red(player, "There is already an invite sent to %s. Though they ".formatted(joining.getName()));
            inviteMessage(joining, tribe);
            return;
        }
        DTribeInvite invite = new DTribeInvite(player.getUniqueId(), joiningUUID, tribe);
        invite.save();
        aqua(player, "Invite has been sent to " + joining.getName());
        inviteMessage(joining, tribe);
    }

    private void inviteMessage(OfflinePlayer joining, DTribe tribe) {
        if (joining.getPlayer() == null) return;
        Component inviteMessage = Component.text("You have been invited to join '%s'".formatted(tribe.getName()))
            .appendNewline()
            .append(Component.text("Click here to join!", TextColor.color(0x03befc))
                .clickEvent(ClickEvent.clickEvent(Action.RUN_COMMAND, "/tribe join " + tribe.getName())));
        joining.getPlayer().sendMessage(inviteMessage);
    }

    @Subcommand("join")
    public void join(Player player, DTribeMember tribePlayer, @Name("invite") String name) {
        if (tribePlayer != null) {
            red(player, "You're already in %s. You cannot be in more than one tribe".formatted(tribePlayer.getTribe().getName()));
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

    @Subcommand("rename")
    public void rename(Player player, DTribeMember tribePlayer, @Name("member") String promotee) {

    }

    @Subcommand("promote")
    public void promote(Player player, DTribeMember tribePlayer, @Name("member") String promotee) {
        if (tribePlayer == null) {
        }
    }

    @Subcommand("demote")
    public void demote() {
    }
}
