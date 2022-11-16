package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Log4j2
@AllArgsConstructor
public class ThirstyPlayer {
    private static final Map<Player, ThirstyPlayer> players = new HashMap<>();
    public static final int MAX_THIRST = 1000;
    public static final int MIN_THIRST = 0;
    public static final int CONSUME = 150;

    private final Player player;
    private int thirst;
    private boolean isThirsty;

    public static ThirstyPlayer getPlayer(Player player) {
        return players.get(player);
    }

    private void updateDisplay() {
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;
        if (thirst > MIN_THIRST)
            thirst--;
        String s = "|||||" + thirst + "|||||";
        int thirstLength = (int) (((double) MAX_THIRST) / s.length() * thirst);
        player.sendActionBar(Component.text("[", TextColor.color(0x9c9c9c))
                .append(Component.text(s.substring(0, thirstLength), TextColor.color(0x2a90de)))
                .append(Component.text(s.substring(thirstLength), TextColor.color(0x7a7a7a)))
                .append(Component.text("]", TextColor.color(0x9c9c9c))));
    }

    /*private void updateDisplay() {
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;
        if (thirst > MIN_THIRST)
            thirst--;
        AttributeInstance attributeInstance = new AttributeInstance(Attributes.ARMOR, ignored -> {});
        attributeInstance.setBaseValue(thirst*20d/MAX_THIRST);
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.connection.send(new ClientboundUpdateAttributesPacket(serverPlayer.getId(), List.of(attributeInstance)));
    }*/

    public void resetThirst() {
        thirst = MAX_THIRST;
    }

    public boolean toggleIsThirsty() {
        return isThirsty = !isThirsty;
    }

    private void consume() {
        thirst += CONSUME;
    }

    private void reset() {
        thirst = MAX_THIRST;
    }

    @SneakyThrows
    private void savePlayer() {
        File saveFile = new File(VoltskiyaPlugin.get().getDataFolder(), "players/" + player.getUniqueId() + ".player");
        if (!saveFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            saveFile.mkdirs();
            if (!saveFile.createNewFile())
                log.error("Unable to create save file for {} ({})", player.getName(), player.getUniqueId());
        }
        @Cleanup
        FileWriter fileWriter = new FileWriter(saveFile, false);
        fileWriter.flush();
        fileWriter.write(thirst);
        fileWriter.write(isThirsty ? 1 : 0);
        players.remove(player);
    }

    public static void save() {
        players.values().forEach(ThirstyPlayer::savePlayer);
    }

    @SneakyThrows
    private static void loadPlayer(Player player) {
        File saveFile = new File(VoltskiyaPlugin.get().getDataFolder(), "players/" + player.getUniqueId() + ".player");
        if (!saveFile.exists()) {
            players.put(player, new ThirstyPlayer(player, MAX_THIRST, true));
            return;
        }
        @Cleanup
        FileReader fileReader = new FileReader(saveFile);
        players.put(player, new ThirstyPlayer(player, fileReader.read(), fileReader.read() == 1));
        fileReader.close();
    }

    public static void load() {
        Bukkit.getOnlinePlayers().forEach(ThirstyPlayer::loadPlayer);
    }

    public static void updatePlayers() {
        players.values().forEach(ThirstyPlayer::updateDisplay);
    }

    public static void join(Player player) {
        loadPlayer(player);
    }

    public static void consume(Player player, ItemStack itemStack) {
        Optional.ofNullable(players.get(player)).ifPresentOrElse(ThirstyPlayer::consume, () -> log.error("The unregistered player {} consumed {}!", player.getName(), itemStack));
    }

    public static void leave(Player player) {
        Optional.ofNullable(players.remove(player)).ifPresentOrElse(ThirstyPlayer::savePlayer, () -> log.error("The unregistered player {} left the server!", player.getName()));
    }

    public static void reset(Player player) {
        Optional.ofNullable(players.get(player)).ifPresentOrElse(ThirstyPlayer::reset, () -> log.error("The unregistered player {} died!", player.getName()));
    }
}
