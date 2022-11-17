package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Log4j2
@AllArgsConstructor
public class ThirstyPlayer {

    private static final NamespacedKey DRINK_AMOUNT_KEY = VoltskiyaPlugin.get()
        .namespacedKey("thirst.drink_amount");
    private static final Map<Player, ThirstyPlayer> players = new HashMap<>();
    public static final int MAX_THIRST = 1000;
    public static final int MIN_THIRST = 0;
    public static final int CONSUME = 150;

    private final Player player;
    private int thirst;
    private boolean isThirsty;

    public ThirstyPlayer(Player player) {
        this(player, MAX_THIRST, true);
    }

    private void update() {
        if (player.getGameMode() != GameMode.SURVIVAL || !isThirsty)
            return;
        if (thirst > MIN_THIRST)
            this.thirst -= ThirstConfig.get().getThirstRate();
        List<PotionEffect> effectsToAdd = ThirstConfig.get().getPotionEffects(this.thirst);
        if (!effectsToAdd.isEmpty())
            VoltskiyaPlugin.get()
                .scheduleSyncDelayedTask(() -> player.addPotionEffects(effectsToAdd));
        this.updateDisplay();
    }

    private void updateDisplay() {
        PlayerHud.updateDisplay(player, thirst / (double) MAX_THIRST, .67);
    }

    public void onSprint() {
        if (thirst >= 100)
            return;
        var connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(
            new ClientboundSetHealthPacket((float) player.getHealth(), 6, player.getSaturation()));
        connection.send(
            new ClientboundSetHealthPacket((float) player.getHealth(), player.getFoodLevel(),
                player.getSaturation()));
    }

    public void resetThirst() {
        thirst = MAX_THIRST;
    }

    public boolean toggleIsThirsty() {
        return isThirsty = !isThirsty;
    }

    private void reset() {
        thirst = MAX_THIRST;
    }

    @SneakyThrows
    private void savePlayer() {
        File saveFile = ThirstModule.get().getFile("players", player.getUniqueId() + ".player");
        if (!saveFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            saveFile.getParentFile().mkdirs();
            if (!saveFile.createNewFile())
                log.error("Unable to create save file for {} ({})", player.getName(), player.getUniqueId());
        }
        @Cleanup FileWriter fileWriter = new FileWriter(saveFile, false);
        fileWriter.flush();
        fileWriter.write(thirst);
        fileWriter.write(isThirsty ? 1 : 0);
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
        synchronized (players) {
            Bukkit.getOnlinePlayers().forEach(ThirstyPlayer::loadPlayer);
        }
    }

    public static void updatePlayers() {
        synchronized (players) {
            players.values().forEach(ThirstyPlayer::update);
        }
    }

    public static void save() {
        synchronized (players) {
            players.values().forEach(ThirstyPlayer::savePlayer);
        }
    }

    @NotNull
    public static ThirstyPlayer getPlayer(Player player) {
        synchronized (players) {
            return players.computeIfAbsent(player, ThirstyPlayer::new);
        }
    }

    public static void join(Player player) {
        synchronized (players) {
            loadPlayer(player);
        }
    }


    public void drink(int consumeAmount, boolean isDirty) {
        // todo deal with drinking dirty water
        this.thirst = Math.max(MIN_THIRST, Math.min(MAX_THIRST, this.thirst + consumeAmount));
    }

    public void leave() {
        synchronized (players) {
            Optional.ofNullable(players.remove(player)).ifPresentOrElse(ThirstyPlayer::savePlayer,
                () -> log.error("The unregistered player {} left the server!", player.getName()));
        }
    }

    public static void reset(Player player) {
        Optional.ofNullable(players.get(player)).ifPresentOrElse(ThirstyPlayer::reset,
            () -> log.error("The unregistered player {} died!", player.getName()));
    }
}
