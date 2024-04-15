package com.voltskiya.mechanics.physical.thirst;

import apple.mc.utilities.player.chat.SendMessage;
import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.annotation.CommandAlias;
import com.voltskiya.lib.acf.annotation.CommandCompletion;
import com.voltskiya.lib.acf.annotation.CommandPermission;
import com.voltskiya.lib.acf.annotation.Name;
import com.voltskiya.lib.acf.annotation.Optional;
import com.voltskiya.lib.acf.annotation.Subcommand;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerManager;
import com.voltskiya.mechanics.physical.thirst.item.ThirstItem;
import java.util.Arrays;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@CommandAlias("thirst")
@CommandPermission("volt.thirst")
public class ThirstCommand extends BaseCommand {

    public ThirstCommand() {
        VoltskiyaPlugin.get().registerCommand(this);
        VoltskiyaPlugin.get().getCommandManager().getCommandCompletions()
            .registerStaticCompletion("thirst.items", Arrays.stream(ThirstItem.values()).map(ThirstItem::getId).toList());
    }

    @Subcommand("give")
    @CommandPermission("volt.thirst.give")
    @CommandCompletion("@thirst.items")
    public void give(Player player, @Name("item") String itemArg) {
        ThirstItem item = ThirstItem.fromId(itemArg);
        if (item == null) {
            SendMessage.get().red(player, "No item named '%s'", itemArg);
            return;
        }
        player.getInventory().addItem(item.toFull(false));
    }

    @Subcommand("drink")
    @CommandPermission("volt.thirst.drink")
    @CommandCompletion("@players @range:1-1000")
    public void drink(CommandSender sender, @Name("[player]") @Optional String playerName,
        @Name("[amount]") @Optional Integer amount) {
        Thirst thirst = getPlayerThirst(sender, playerName);
        if (thirst == null) return;
        thirst.drink(amount == null ? Thirst.MAX_THIRST : amount);
        sendSuccess(sender, playerName == null ? "Reset your thirst" : String.format("Reset %s's thirst", playerName));
    }

    @Subcommand("toggle")
    @CommandPermission("volt.thirst.toggle")
    @CommandCompletion("@players")
    public void toggle(CommandSender sender, @Optional @Name("[player]") String playerName) {
        Thirst thirst = getPlayerThirst(sender, playerName);
        if (thirst == null) return;
        boolean isThirsty = thirst.toggleIsThirsty();

        Component onOrOff = isThirsty ? Component.text("on", NamedTextColor.GREEN) : Component.text("off", NamedTextColor.RED);
        String playerMsg = playerName == null ? "Your thirst is now " : String.format("%s's thirst is now ", playerName);

        sender.sendMessage(Component.text(playerMsg, NamedTextColor.AQUA).append(onOrOff));
    }

    private Thirst getPlayerThirst(CommandSender sender, @Nullable String playerName) {
        Player player;
        if (playerName != null) {
            player = Bukkit.getPlayer(playerName);
        } else {
            player = sender instanceof Player ? (Player) sender : null;
        }
        if (player == null) {
            sendError(sender,
                null == playerName ? "Please specify a player" : String.format("Could not find player '%s'", playerName));
            return null;
        }
        return PhysicalPlayerManager.getPlayer(player).getThirst();
    }

    private void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.AQUA));
    }

    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.RED));
    }
}