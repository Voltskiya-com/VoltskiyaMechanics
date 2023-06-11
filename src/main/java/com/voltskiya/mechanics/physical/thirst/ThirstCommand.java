package com.voltskiya.mechanics.physical.thirst;

import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.annotation.CommandAlias;
import com.voltskiya.lib.acf.annotation.CommandCompletion;
import com.voltskiya.lib.acf.annotation.CommandPermission;
import com.voltskiya.lib.acf.annotation.Name;
import com.voltskiya.lib.acf.annotation.Optional;
import com.voltskiya.lib.acf.annotation.Subcommand;
import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import java.util.Arrays;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@CommandAlias("thirst")
public class ThirstCommand extends BaseCommand {

    public ThirstCommand() {
        VoltskiyaPlugin.get().registerCommand(this);
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions()
            .registerStaticCompletion("volt_items", Arrays.stream(Item.values()).map(Enum::name).toList());
    }

    @Subcommand("give")
    @CommandPermission("volt.thirst.give")
    @CommandCompletion("@volt_items")
    public void give(Player player, @Name("item") String item) {
        player.getInventory().addItem(Item.valueOf(item).toItemStack());
    }

    @Subcommand("drink")
    @CommandPermission("volt.thirst.drink")
    @CommandCompletion("@players @range:1-1000")
    public void drink(CommandSender sender, @Name("[player]") @Optional String playerName,
        @Name("[amount]") @Optional Integer amount) {
        Player player = getPlayer(sender, playerName);
        if (null == player) {
            sendPlayerNotFoundError(sender, playerName);
            return;
        }
//        VoltskiyaPlayerManager.getPlayer(player).getThirst().drink(Objects.requireNonNullElse(amount, Thirst.MAX_THIRST));
        sendSuccess(sender, null == playerName ? "Reset your thirst" : String.format("Reset %s's thirst", playerName));
    }

    @Subcommand("toggle")
    @CommandPermission("volt.thirst.toggle")
    @CommandCompletion("@players")
    public void toggle(CommandSender sender, @Optional @Name("[player]") String playerName) {
        Player player = getPlayer(sender, playerName);
        if (null == player) {
            sendPlayerNotFoundError(sender, playerName);
        }
//        boolean isThirsty = VoltskiyaPlayerManager.getPlayer(player).getThirst().toggleIsThirsty();
//        Component onOrOff = isThirsty ? Component.text("on", NamedTextColor.GREEN) : Component.text("off", NamedTextColor.RED);
//        sender.sendMessage(null == playerName ? Component.text("Your thirst is now ", NamedTextColor.AQUA).append(onOrOff)
//            : Component.text(String.format("%s's thirst is now ", playerName), NamedTextColor.AQUA).append(onOrOff));
    }

    private void sendPlayerNotFoundError(CommandSender sender, String playerName) {
        sendError(sender, null == playerName ? "Please specify a player" : String.format("Could not find player '%s'", playerName));
    }

    @Nullable
    private Player getPlayer(CommandSender sender, @Nullable String playerName) {
        if (null != playerName)
            return Bukkit.getPlayer(playerName);
        return sender instanceof Player ? (Player) sender : null;
    }

    private void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.AQUA));
    }

    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.RED));
    }
}