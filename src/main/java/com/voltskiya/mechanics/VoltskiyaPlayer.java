package com.voltskiya.mechanics;

import com.voltskiya.mechanics.stamina.Stamina;
import com.voltskiya.mechanics.thirst.Thirst;
import com.voltskiya.mechanics.thirst.ThirstModule;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Getter
@Setter
public class VoltskiyaPlayer {
    private static final Map<Player, VoltskiyaPlayer> players = new HashMap<>();

    private final Player player;
    private final Display display;

    private final Thirst thirst;
    private final Stamina stamina;

    public VoltskiyaPlayer(Player player) {
        this(player, 1000, true, 1000, false);
    }

    public VoltskiyaPlayer(Player player, int thirst, boolean isThirsty, int stamina, boolean outOfStamina) {
        this.player = player;
        this.thirst = new Thirst(player, thirst, isThirsty);
        this.stamina = new Stamina(player, stamina, outOfStamina);
        display = new Display(this.player);
    }

    private void update() {
        if (GameMode.SURVIVAL != player.getGameMode())
            return;
        stamina.updateStamina();
        thirst.updateThirst();
        display.updateDisplay(thirst.getThirstPercentage(), stamina.getStaminaPercentage());
    }

    public void onSprint() {
        if (!thirst.shouldDisableSprint() || stamina.shouldDisableSprint())
            return;

        var connection = ((CraftPlayer) player).getHandle().connection;
        float saturation = player.getSaturation();
        double health = player.getHealth();
        connection.send(new ClientboundSetHealthPacket((float)health, 6, saturation));
        connection.send(new ClientboundSetHealthPacket((float)health, player.getFoodLevel(), saturation));
    }

    private void reset() {
        thirst.reset();
        stamina.reset();
    }


    @SneakyThrows
    private void savePlayer() {
        File saveFile = ThirstModule.get().getFile("players", player.getUniqueId() + ".player");
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
            if (!saveFile.createNewFile())
                log.error("Unable to create save file for {} ({})", player.getName(), player.getUniqueId());
        }


        @Cleanup
        FileWriter fileWriter = new FileWriter(saveFile, false);
        fileWriter.write(thirst.getThirst());
        fileWriter.write(thirst.isThirsty() ? 1 : 0);
        fileWriter.write(stamina.getStamina());
        fileWriter.write(stamina.isOutOfStamina() ? 1 : 0);
    }

    @SneakyThrows
    private static VoltskiyaPlayer loadPlayer(Player player) {
        File saveFile = ThirstModule.get().getFile("players", player.getUniqueId() + ".player");
        if (!saveFile.exists()) {
            VoltskiyaPlayer voltskiyaPlayer = new VoltskiyaPlayer(player);
            players.put(player, voltskiyaPlayer);
            return voltskiyaPlayer;
        }
        @Cleanup
        FileReader fileReader = new FileReader(saveFile);
        VoltskiyaPlayer var4;
        VoltskiyaPlayer voltskiyaPlayer = new VoltskiyaPlayer(player, fileReader.read(), 1 == fileReader.read(), fileReader.read(), 1 == fileReader.read());
        players.put(player, voltskiyaPlayer);
        fileReader.close();
        return voltskiyaPlayer;
    }

    public static synchronized void load() {
        Bukkit.getOnlinePlayers().forEach(VoltskiyaPlayer::loadPlayer);
    }

    public static synchronized void updatePlayers() {
        players.values().forEach(VoltskiyaPlayer::update);
    }

    public static synchronized void save() {
        players.values().forEach(VoltskiyaPlayer::savePlayer);
    }

    @NotNull
    public static synchronized VoltskiyaPlayer getPlayer(Player player) {
        return players.computeIfAbsent(player, VoltskiyaPlayer::new);
    }

    public static synchronized VoltskiyaPlayer join(Player player) {
        return loadPlayer(player);
    }

    public synchronized void leave() {
        Optional.ofNullable(players.remove(player)).ifPresentOrElse(VoltskiyaPlayer::savePlayer, () ->
                log.error("The unregistered player {} left the server!", player.getName()));
    }

    public static synchronized void reset(Player player) {
        Optional.ofNullable(players.get(player)).ifPresentOrElse(VoltskiyaPlayer::reset, () ->
                log.error("The unregistered player {} died!", player.getName()));
    }
}
