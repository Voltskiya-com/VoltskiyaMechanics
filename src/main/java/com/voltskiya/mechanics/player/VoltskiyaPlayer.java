package com.voltskiya.mechanics.player;

import apple.utilities.database.SaveFileable;
import com.voltskiya.mechanics.Display;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import com.voltskiya.mechanics.stamina.Stamina;
import com.voltskiya.mechanics.thirst.Thirst;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor // for gson
@Getter
public class VoltskiyaPlayer implements SaveFileable {

    private final transient Display display = new Display();
    private transient Player player;
    private Thirst thirst;
    private Stamina stamina;

    VoltskiyaPlayer(@NotNull Player player) {
        thirst = new Thirst();
        stamina = new Stamina();
        onLoad(player);
    }

    public static String getSaveFileName(UUID uuid) {
        return uuid.toString() + ".player.json";
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

    public void verifySprint() {
        if (!stamina.isLowSprint()) return;
        Player player = this.player.getPlayer();
        if (player != null)
            player.addPotionEffect(Stamina.LOW_SPRINT_EFFECT);
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

    public ItemStack onConsume(VoltskiyaItemStack itemStack, ItemStack replacement) {
        return thirst.onConsume(itemStack, replacement);
    }
}
