package com.voltskiya.mechanics.player;

import apple.utilities.database.SaveFileable;
import com.voltskiya.mechanics.Display;
import com.voltskiya.mechanics.stamina.Stamina;
import com.voltskiya.mechanics.thirst.Thirst;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor // for gson
@Getter
public class VoltskiyaPlayer implements SaveFileable {

    private PlayerSerialized player;
    private Display display;

    private Thirst thirst;
    private Stamina stamina;

    public VoltskiyaPlayer(@NotNull Player player) {
        this.player = new PlayerSerialized(player);
        this.thirst = new Thirst();
        this.stamina = new Stamina();
        this.display = new Display();
        this.load();
    }

    private void load() {
        this.thirst.load(this);
        this.stamina.load(this);
        this.display.load(this);
    }

    public void update() {
        Player player = this.player.getPlayer();
        if (player == null) return;
        if (GameMode.SURVIVAL != player.getGameMode()) return;
        stamina.updateStamina();
        thirst.updateThirst();
        display.updateDisplay(thirst.getThirstPercentage(), stamina.getStaminaPercentage());
    }

    public void onSprint() {
        if (!thirst.shouldDisableSprint() || stamina.shouldDisableSprint()) return;

        Player player = this.player.getPlayer();
        if (player == null) return;
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
        this.display.leave();
        VoltskiyaPlayerManager.remove(player.uuid());
    }

    @Override
    public String getSaveFileName() {
        return getSaveFileName(player.uuid());
    }

    public static String getSaveFileName(UUID uuid) {
        return uuid.toString() + ".player.json";
    }

}
