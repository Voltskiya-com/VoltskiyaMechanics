package com.voltskiya.mechanics.player;

import apple.utilities.database.SaveFileable;
import com.voltskiya.mechanics.Display;
import com.voltskiya.mechanics.stamina.Stamina;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor // for gson
@Getter
public class VoltskiyaPlayer implements SaveFileable {

    private final transient Display display = new Display();
    private transient Player player;
    //    private Thirst thirst;
    private Stamina stamina;

    VoltskiyaPlayer(@NotNull Player player) {
//        thirst = new Thirst();
        stamina = new Stamina();
        onLoad(player);
    }

    public static String getSaveFileName(UUID uuid) {
        return uuid.toString() + ".player.json";
    }

    void onLoad(Player player) {
        this.player = player;
//        thirst.onLoad(player);
        stamina.onLoad(this);
        display.onLoad(player);
    }

    void onTick() {
        if (GameMode.ADVENTURE != player.getGameMode() && GameMode.SURVIVAL != player.getGameMode()) return;
        stamina.onTick();
//        thirst.onTick();
        NamespacedKey key = new NamespacedKey("thirstcore", "thirst");
        int maxThirst = 1000;
        Integer playerThirst = player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, maxThirst);
        double thirstPercentage = (double) playerThirst / maxThirst;

        display.updateDisplay(thirstPercentage, stamina.getStaminaPercentage());
    }

    public void verifySprint() {
        if (!stamina.isLowSprint()) return;
        Player player = this.player.getPlayer();
        if (player != null)
            player.addPotionEffect(Stamina.LOW_SPRINT_EFFECT);
    }

    public void onDeath() {
        stamina.onDeath();
    }

    public void onLeave() {
        display.remove();
        VoltskiyaPlayerManager.remove(player.getUniqueId());
    }

    @Override
    public String getSaveFileName() {
        return getSaveFileName(player.getUniqueId());
    }

//    public ItemStack onConsume(VoltskiyaItemStack itemStack, ItemStack replacement) {
//        return thirst.onConsume(itemStack, replacement);
//    }
}
