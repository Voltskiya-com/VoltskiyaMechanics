package com.voltskiya.mechanics.physical.player;

import apple.utilities.database.concurrent.ConcurrentAJD;
import apple.utilities.database.concurrent.group.ConcurrentAJDTyped;
import com.voltskiya.mechanics.physical.PhysicalModule;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PhysicalPlayerManager {

    private static final Map<UUID, PhysicalPlayer> players = new HashMap<>();
    private static ConcurrentAJDTyped<PhysicalPlayer> manager;

    public static void remove(UUID uuid) {
        synchronized (players) {
            PhysicalPlayer removed = players.remove(uuid);
            if (removed != null) manager.saveInFolder(removed);
        }
    }

    public static void fetchPlayer(Player bukkitPlayer) {
        getPlayer(bukkitPlayer);
//        UUID uuid = bukkitPlayer.getUniqueId();
//        synchronized (players) {
//            if (players.containsKey(uuid)) return;
//        }
//        manager.loadOne(PhysicalPlayer.getSaveFileName(uuid))
//            .thenAccept((player) -> {
//                player.onLoad(bukkitPlayer);
//                synchronized (players) {
//                    players.put(uuid, player);
//                }
//            });
//

    }

    public static PhysicalPlayer getPlayer(Player bukkitPlayer) {
        UUID uuid = bukkitPlayer.getUniqueId();
        PhysicalPlayer player;
        synchronized (players) {
            player = players.get(uuid);
        }
        if (player != null)
            return player;

        player = manager.loadOneNow(PhysicalPlayer.getSaveFileName(uuid));
        if (player == null) player = new PhysicalPlayer();

        player.onLoad(bukkitPlayer);
        synchronized (players) {
            players.put(uuid, player);
        }
        return player;
    }

    public static void load() {
        File folder = PhysicalModule.get().getFile("players");
        manager = ConcurrentAJD.createTyped(PhysicalPlayer.class, folder);
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

    public static void onDisable() {
        synchronized (players) {
            players.values().forEach(PhysicalPlayer::onDisable);
        }
    }
}
