package com.voltskiya.mechanics.player;

import apple.utilities.database.SaveFileable;
import com.voltskiya.mechanics.Display;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import com.voltskiya.mechanics.stamina.Stamina;
import com.voltskiya.mechanics.thirst.Thirst;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@NoArgsConstructor // for gson
@Getter
public class VoltskiyaPlayer implements SaveFileable {

    private transient Player player;
    private final transient Display display = new Display();

    private Thirst thirst;
    private Stamina stamina;

    VoltskiyaPlayer(@NotNull Player player) {
        thirst = new Thirst();
        stamina = new Stamina();
        onLoad(player);
    }

    void onLoad(Player player) {
        this.player = player;
        thirst.onLoad(player);
        stamina.onLoad(this);
        display.onLoad(player);
    }

    void onTick() {
        if (GameMode.SURVIVAL != player.getGameMode()) return;
        stamina.onTick();
        thirst.onTick();
        display.updateDisplay(thirst.getThirstPercentage(), stamina.getStaminaPercentage());
    }

    public void onSprint() {
        if (!thirst.shouldDisableSprint() || !stamina.shouldDisableSprint()) return;
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        float saturation = player.getSaturation();
        double health = player.getHealth();
        connection.send(new ClientboundSetHealthPacket((float) health, 6, saturation));
        connection.send(new ClientboundSetHealthPacket((float) health, player.getFoodLevel(), saturation));
    }

    public void onDeath() {
        thirst.onDeath();
        stamina.onDeath();
    }


    public void onLeave() {
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

    public ItemStack onConsume(VoltskiyaItemStack itemStack, ItemStack replacement) {
        return thirst.onConsume(itemStack, replacement);
    }
}
