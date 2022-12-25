package com.voltskiya.mechanics.player;

import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDTyped;
import com.voltskiya.lib.pmc.FileIOServiceNow;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VoltskiyaPlayerManager {

    private static final Map<UUID, VoltskiyaPlayer> players = new HashMap<>();
    private static AppleAJDTyped<VoltskiyaPlayer> manager;

    public static void savePlayer(VoltskiyaPlayer player) {
        manager.saveInFolder(player);
    }

    public static void remove(UUID uuid) {
        synchronized (players) {
            players.remove(uuid);
        }
    }

    public static VoltskiyaPlayer getPlayer(Player bukkitPlayer) {
        UUID uuid = bukkitPlayer.getUniqueId();
        VoltskiyaPlayer player;
        synchronized (players) {
            player = players.get(uuid);
        }
        if (player != null)
            return player;
        player = manager.loadFromFolderNow(VoltskiyaPlayer.getSaveFileName(uuid));
        if (player == null)
            player = new VoltskiyaPlayer(bukkitPlayer);
        player.setPlayer(bukkitPlayer);
        player.load();
        synchronized (players) {
            players.put(uuid, player);
        }
        return player;
    }

    public static void load() {
        File file = new File(VoltskiyaPlugin.get().getDataFolder(), "players");
        manager = AppleAJD.createTyped(VoltskiyaPlayer.class, file, FileIOServiceNow.taskCreator());
        Bukkit.getOnlinePlayers().forEach(VoltskiyaPlayerManager::getPlayer);
    }

    public static void updatePlayers() {
        synchronized (players) {
            players.values().forEach(VoltskiyaPlayer::update);
        }
    }

    public static void saveNow() {
        synchronized (players) {
            players.values().forEach(manager::saveInFolderNow);
        }
    }

    public static void save() {
        synchronized (players) {
            players.values().forEach(manager::saveInFolder);
        }
    }
}
