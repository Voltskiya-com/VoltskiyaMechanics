package com.voltskiya.mechanics.physical.player;

import apple.utilities.database.SaveFileable;
import com.voltskiya.mechanics.physical.stamina.Stamina;
import com.voltskiya.mechanics.physical.thirst.Thirst;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PhysicalPlayer implements SaveFileable {

    private final transient ActionBarDisplay display = new ActionBarDisplay();
    private final Thirst thirst;
    private final Stamina stamina;
    private transient Player player;

    public PhysicalPlayer() {
        thirst = new Thirst();
        stamina = new Stamina();
    }

    public static String getSaveFileName(UUID uuid) {
        return uuid.toString() + ".player.json";
    }

    void onLoad(Player player) {
        this.player = player;
        this.thirst.onLoad(player);
        this.stamina.onLoad(this);
        this.display.onLoad(player);
    }

    void onTick() {
        if (GameMode.ADVENTURE != player.getGameMode() && GameMode.SURVIVAL != player.getGameMode()) return;
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
        stamina.onDeath();
    }

    public void onLeave() {
        display.remove();
        PhysicalPlayerManager.remove(player.getUniqueId());
    }

    @Override
    public String getSaveFileName() {
        return getSaveFileName(player.getUniqueId());
    }

    public Thirst getThirst() {
        return thirst;
    }

    public Stamina getStamina() {
        return stamina;
    }

    public Player getPlayer() {
        return this.player;
    }
}
