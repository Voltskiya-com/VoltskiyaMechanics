package com.voltskiya.mechanics.player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerSerialized {

    @Nullable
    private transient Player player;
    private UUID uuid;

    public PlayerSerialized(@NotNull Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    public PlayerSerialized() {
    }

    public void ifPresent(Consumer<Player> consumer) {
        Player player = getPlayer();
        if (player != null) consumer.accept(player);
    }

    public <T> T getOrDefault(Function<Player, T> consumer, T defaultIfNull) {
        Player player = getPlayer();
        if (player == null) return defaultIfNull;
        T result = consumer.apply(player);
        if (result == null) return defaultIfNull;
        return result;
    }

    @Nullable
    public Player getPlayer() {
        if (this.player != null) return this.player;
        return this.player = Bukkit.getPlayer(uuid);
    }

    public UUID uuid() {
        return this.uuid;
    }
}
