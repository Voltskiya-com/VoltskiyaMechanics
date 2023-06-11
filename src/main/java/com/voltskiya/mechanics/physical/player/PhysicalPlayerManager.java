package com.voltskiya.mechanics.physical.player;

import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDTyped;
import com.voltskiya.lib.pmc.FileIOServiceNow;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PhysicalPlayerManager {

    private static final Map<UUID, PhysicalPlayer> players = new HashMap<>();
    private static AppleAJDTyped<PhysicalPlayer> manager;

    public static void remove(UUID uuid) {
        synchronized (players) {
            players.remove(uuid);
        }
    }

    public static void fetchPlayer(Player bukkitPlayer) {
        UUID uuid = bukkitPlayer.getUniqueId();
        synchronized (players) {
            if (players.containsKey(uuid)) return;
        }
        manager.loadFromFolder(PhysicalPlayer.getSaveFileName(uuid))
            .onSuccess((player) -> {
                player.onLoad(bukkitPlayer);
                synchronized (players) {
                    players.put(uuid, player);
                }
            });


    }

    public static PhysicalPlayer getPlayer(Player bukkitPlayer) {
        UUID uuid = bukkitPlayer.getUniqueId();
        PhysicalPlayer player;
        synchronized (players) {
            player = players.get(uuid);
        }
        if (player != null)
            return player;

        player = manager.loadFromFolderNow(PhysicalPlayer.getSaveFileName(uuid));

        if (player == null)
            player = new PhysicalPlayer(bukkitPlayer);
        else
            player.onLoad(bukkitPlayer);
        synchronized (players) {
            players.put(uuid, player);
        }
        return player;
    }

    public static void load() {
        File file = new File(VoltskiyaPlugin.get().getDataFolder(), "players");
        manager = AppleAJD.createTyped(PhysicalPlayer.class, file, FileIOServiceNow.taskCreator());
        Bukkit.getOnlinePlayers().forEach(PhysicalPlayerManager::getPlayer);
    }

    public static void tickPlayers() {
        synchronized (players) {
            players.values().forEach(PhysicalPlayer::onTick);
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
