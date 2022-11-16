package com.voltskiya.mechanics.thirst;

import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.annotation.CommandAlias;
import com.voltskiya.lib.acf.annotation.CommandCompletion;
import com.voltskiya.lib.acf.annotation.CommandPermission;
import com.voltskiya.lib.acf.annotation.Name;
import com.voltskiya.lib.acf.annotation.Optional;
import com.voltskiya.lib.acf.annotation.Subcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@CommandAlias("thirst")
public class ThirstCommandACF extends BaseCommand {

    public ThirstCommandACF() {
        ThirstModule.registerCommand(this);
    }

    @Subcommand("drink")
    @CommandPermission("volt.thirst.drink")
    @CommandCompletion("@players @range:1-1000")
    public void drink(CommandSender sender, @Name("[player]") @Optional() String playerName,
        @Name("[amount]") @Optional Integer amount) {
        Player player = getPlayer(sender, playerName);
        if (player == null) {
            sendPlayerNotFoundError(sender, playerName);
            return;
        }

        if (amount == null)
            ThirstyPlayer.getPlayer(player).resetThirst();
        else
            ThirstyPlayer.getPlayer(player).consume(amount);

        sendSuccess(sender, playerName == null ? "Reset your thirst"
            : String.format("Reset %s's thirst", playerName));
    }

    @Subcommand("toggle")
    @CommandPermission("volt.thirst.toggle")
    @CommandCompletion("@players")
    public void toggle(CommandSender sender, @Optional() @Name("[player]") String playerName) {
        Player player = getPlayer(sender, playerName);
        if (player == null) {
            sendPlayerNotFoundError(sender, playerName);
            return;
        }
        boolean isThirsty = ThirstyPlayer.getPlayer(player).toggleIsThirsty();
        Component onOrOff = isThirsty ? Component.text("on", NamedTextColor.GREEN)
            : Component.text("off", NamedTextColor.RED);
        sender.sendMessage(
            playerName == null ? Component.text("Your thirst is now ", NamedTextColor.AQUA)
                .append(onOrOff) : Component.text(String.format("%s's thirst is now ", playerName),
                NamedTextColor.AQUA).append(onOrOff));
    }

    private void sendPlayerNotFoundError(CommandSender sender, String playerName) {
        sendError(sender, playerName == null ? "Please specify a player"
            : String.format("Could not find player '%s'", playerName));
    }


    private @Nullable Player getPlayer(CommandSender sender, @Nullable String playerName) {
        // if the player is specified in the command, get that player first
        if (playerName != null)
            return Bukkit.getPlayer(playerName);
        if (sender instanceof Player player)
            return player;
        return null;
    }


    private void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.AQUA));
    }

    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.RED));
    }
}
