package com.voltskiya.mechanics.player;

import apple.utilities.database.SaveFileable;
import com.voltskiya.mechanics.Display;
import com.voltskiya.mechanics.stamina.Stamina;
import com.voltskiya.mechanics.thirst.Thirst;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@NoArgsConstructor // for gson
public class VoltskiyaPlayer implements SaveFileable {

    @Setter
    @Getter
    private transient Player player;
    private transient Display display = new Display();

    private Thirst thirst;
    @Getter
    private Stamina stamina;

    public VoltskiyaPlayer(@NotNull Player player) {
        this.player = player;
        thirst = new Thirst();
        stamina = new Stamina();
        load();
    }

    void load() {
        thirst.load(this);
        stamina.load(this);
        display.load(this);
    }

    public void update() {
        if (GameMode.SURVIVAL != player.getGameMode()) return;
        stamina.updateStamina();
        thirst.updateThirst();
        display.updateDisplay(thirst.getThirstPercentage(), stamina.getStaminaPercentage());
    }

    public void onSprint() {
        if (!thirst.shouldDisableSprint() || stamina.shouldDisableSprint()) return;
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        float saturation = player.getSaturation();
        double health = player.getHealth();
        connection.send(new ClientboundSetHealthPacket((float) health, 6, saturation));
        connection.send(new ClientboundSetHealthPacket((float) health, player.getFoodLevel(), saturation));
    }

    public void reset() {
        thirst.reset();
        stamina.reset();
    }


    public void leave() {
        display.cancelTask();
        VoltskiyaPlayerManager.remove(player.getUniqueId());
    }

    @Override
    public String getSaveFileName() {
        return getSaveFileName(player.getUniqueId());
    }

    public static String getSaveFileName(UUID uuid) {
        return uuid.toString() + ".player.json";
    }
}
