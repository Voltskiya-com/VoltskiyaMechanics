package com.voltskiya.mechanics.tribe.command;

import apple.mc.utilities.player.chat.SendMessage;
import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.annotation.CommandAlias;
import com.voltskiya.lib.acf.annotation.CommandPermission;
import com.voltskiya.lib.acf.annotation.Subcommand;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.tribe.database.Tribe;
import org.bukkit.entity.Player;

@CommandAlias("tribe")
@CommandPermission("volt.tribe")
public class TribeManageCommand extends BaseCommand implements SendMessage {

    public TribeManageCommand() {
        VoltskiyaPlugin.get().registerCommand(this);
        VoltskiyaPlugin.get().getCommandManager().getCommandContexts()
            .registerIssuerAwareContext(Tribe.class, ctx -> Tribe.findPlayer(ctx.getPlayer().getUniqueId()));
    }

    @Subcommand("create")
    public void create(Player player, Tribe tribe, String name) {
        if (tribe != null) {
            red(player, "You are already a member of the tribe: %s".formatted(tribe));
            return;
        }
        Tribe.createAndRegister(name,player);
    }

    @Subcommand("rename")
    public void rename() {
    }

    @Subcommand("promote")
    public void promote() {
    }

    @Subcommand("demote")
    public void demote() {
    }
}
