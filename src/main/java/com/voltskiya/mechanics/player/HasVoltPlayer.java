package com.voltskiya.mechanics.player;

import java.util.function.Consumer;
import org.bukkit.entity.Player;

public interface HasVoltPlayer {

    VoltskiyaPlayer getVolt();

    default PlayerSerialized player() {
        return getVolt().getPlayer();
    }

    default void playerIfPresent(Consumer<Player> consumer) {
        getVolt().getPlayer().ifPresent(consumer);
    }
}
