package com.voltskiya.mechanics.tribe.command;

import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.BukkitCommandCompletionContext;
import com.voltskiya.lib.acf.BukkitCommandExecutionContext;
import com.voltskiya.lib.acf.PaperCommandManager;
import com.voltskiya.lib.acf.annotation.CommandAlias;
import com.voltskiya.lib.acf.annotation.CommandCompletion;
import com.voltskiya.lib.acf.annotation.CommandPermission;
import com.voltskiya.lib.acf.annotation.Name;
import com.voltskiya.lib.acf.annotation.Optional;
import com.voltskiya.lib.acf.annotation.Single;
import com.voltskiya.lib.acf.annotation.Subcommand;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.tribe.entity.member.DTribeMember;
import com.voltskiya.mechanics.tribe.query.TribeStorage;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("tribe")
@CommandPermission("volt.tribe")
public class TribeCommand extends BaseCommand implements TribeCommandMembership, TribeCommandClaim, TribeCommandInfo {

    public TribeCommand() {
        PaperCommandManager cmd = VoltskiyaPlugin.get().getCommandManager();
        cmd.getCommandCompletions().registerAsyncCompletion("tribe_members", TribeCommand::getMembers);
        cmd.getCommandCompletions().registerAsyncCompletion("tribe_invites", TribeCommand::getInvites);
        cmd.getCommandContexts().registerIssuerAwareContext(DTribeMember.class, TribeCommand::getMember);

        VoltskiyaPlugin.get().registerCommand(this);
    }

    private static Collection<String> getMembers(BukkitCommandCompletionContext c) {
        UUID uuid = c.getPlayer().getUniqueId();
        DTribeMember member = TribeStorage.findPlayer(uuid);
        if (member == null) return Collections.emptyList();

        return member.getTribe().getMembers()
            .stream()
            .filter(p -> !p.getPlayerId().equals(uuid))
            .map(p -> p.getPlayer().getName()).toList();
    }

    private static Collection<String> getInvites(BukkitCommandCompletionContext c) {
        UUID uuid = c.getPlayer().getUniqueId();
        DTribeMember member = TribeStorage.findPlayer(uuid);
        if (member == null) return Collections.emptyList();

        return member.getTribe().getInvites()
            .stream()
            .map(inv -> inv.getTargetPlayer().getName()).toList();
    }

    private static DTribeMember getMember(BukkitCommandExecutionContext ctx) {
        return TribeStorage.findPlayer(ctx.getPlayer().getUniqueId());
    }


    @Subcommand("claims")
    public class TribeSubCommandClaim extends BaseCommand {

        @Subcommand("claim")
        @CommandCompletion("@nothing")
        public void cmdClaim(Player player, DTribeMember member) {
            claim(player, member);
        }

        @Subcommand("unclaim")
        @CommandCompletion("@nothing")
        public void cmdUnClaim(Player player, DTribeMember member, @Optional @Name("chunk_key") Long chunk) {
            unclaim(player, member, chunk);
        }

        @Subcommand("list")
        @CommandCompletion("@nothing")
        public void cmdList(Player player, DTribeMember member) {
            claimList(player, member);
        }

    }

    @Subcommand("manage")
    public class TribeSubCommandManage extends BaseCommand {

        @Subcommand("rename")
        public void rename(Player player, DTribeMember tribePlayer, @Name("member") String promotee) {

        }
    }

    @Subcommand("info")
    public class TribeSubCommandInfo extends BaseCommand {

        @Subcommand("list")
        @CommandCompletion("@nothing")
        public void cmdList(Player player, DTribeMember tribePlayer) {
            list(player, tribePlayer);
        }
    }

    @Subcommand("rank")
    public class TribeSubCommandRank extends BaseCommand {

        @Subcommand("promote")
        public void promote(Player player, DTribeMember tribePlayer, @Name("member") String promotee) {
            if (tribePlayer == null) {
            }
        }

        @Subcommand("demote")
        public void demote() {
        }
    }

    @Subcommand("membership")
    public class TribeSubCommandMembership extends BaseCommand {

        @Subcommand("create")
        @CommandCompletion("[tag] [name]")
        public void cmdCreate(Player player, DTribeMember tribePlayer, @Single @Name("tag") String tag, @Name("name") String name) {
            create(player, tribePlayer, name, tag);
        }

        @Subcommand("invite")
        @CommandCompletion("@players")
        public void cmdInvite(Player player, DTribeMember tribePlayer, OfflinePlayer joining) {
            invite(player, tribePlayer, joining);
        }


        @Subcommand("join")
        public void cmdJoin(Player player, DTribeMember tribePlayer, @Name("invite") String name) {
            join(player, tribePlayer, name);
        }

        @Subcommand("kick")
        @CommandCompletion("@tribe_members|@tribe_invites")
        public void cmdKick(Player player, DTribeMember tribePlayer, @Name("member") OfflinePlayer member) {
            kick(player, tribePlayer, member);
        }

        @Subcommand("leave")
        @CommandCompletion("@nothing")
        public void cmdLeave(Player player, DTribeMember member, @Optional @Name("confirm") String confirm) {
            leave(player, member, confirm);
        }
    }
}
