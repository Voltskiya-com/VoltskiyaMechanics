package com.voltskiya.mechanics.physical.temperature;

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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@CommandAlias("temperature")
@CommandPermission("volt.temperature")
public class TemperatureCommand extends BaseCommand implements SendMessage {

    public TemperatureCommand() {
        VoltskiyaPlugin.get().registerCommand(this);
    }

    @Subcommand("toggle")
    @CommandPermission("volt.temperature.toggle")
    @CommandCompletion("@players")
    public void toggle(CommandSender sender, @Optional @Name("[player]") String playerName) {
        Temperature temperature = getPlayertemperature(sender, playerName);
        if (temperature == null) return;
        boolean isActive = temperature.toggleIsActive();

        Component onOrOff = isActive ? Component.text("on", NamedTextColor.GREEN) : Component.text("off", NamedTextColor.RED);
        String playerMsg = playerName == null ? "Your temperature is now " : String.format("%s's temperature is now ", playerName);

        sender.sendMessage(Component.text(playerMsg, NamedTextColor.AQUA).append(onOrOff));
    }

    private Temperature getPlayertemperature(CommandSender sender, @Nullable String playerName) {
        Player player;
        if (playerName != null) {
            player = Bukkit.getPlayer(playerName);
        } else {
            player = sender instanceof Player ? (Player) sender : null;
        }
        if (player == null) {
            red(sender,
                null == playerName ? "Please specify a player" : String.format("Could not find player '%s'", playerName));
            return null;
        }
        return PhysicalPlayerManager.getPlayer(player).getTemperature();
    }
}
