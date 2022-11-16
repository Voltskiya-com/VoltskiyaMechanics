package com.voltskiya.mechanics.thirst;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        if (thirst < 200)
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 0, false, true, true));
        if (thirst < 100)
            player.setSprinting(false);
        if (thirst == 0)
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 0, false, true, true));

        String s = "|||||" + String.format("%02d", thirst / 10) + "|||||";
        int thirstLength = (int) Math.round(((double) MAX_THIRST) / s.length() * thirst);
        player.sendActionBar(Component.text("[", TextColor.color(0x9c9c9c))
                .append(Component.text(s.substring(0, thirstLength), TextColor.color(0x2a90de)))
                .append(Component.text(s.substring(thirstLength), TextColor.color(0x7a7a7a)))
                .append(Component.text("]", TextColor.color(0x9c9c9c))));
    }

    public void onSprint() {
        if (thirst >= 100)
            return;
        var connection = ((CraftPlayer)player).getHandle().connection;
        connection.send(new ClientboundSetHealthPacket((float) player.getHealth(), 6, player.getSaturation()));
        connection.send(new ClientboundSetHealthPacket((float) player.getHealth(), player.getFoodLevel(), player.getSaturation()));
    }

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
        File saveFile = ThirstModule.get().getFile("players", player.getUniqueId() + ".player");
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
        File saveFile = ThirstModule.get().getFile("players", player.getUniqueId() + ".player");
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
