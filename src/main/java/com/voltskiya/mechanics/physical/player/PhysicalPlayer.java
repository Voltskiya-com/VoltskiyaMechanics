package com.voltskiya.mechanics.physical.player;

import apple.utilities.database.SaveFileable;
import com.voltskiya.mechanics.physical.stamina.Stamina;
import com.voltskiya.mechanics.physical.temperature.Temperature;
import com.voltskiya.mechanics.physical.thirst.Thirst;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PhysicalPlayer implements SaveFileable {

    private final transient ActionBarDisplay display = new ActionBarDisplay();
    private final Thirst thirst;
    private final Stamina stamina;
    private final Temperature temperature;
    private transient Player player;

    public PhysicalPlayer() {
        thirst = new Thirst();
        stamina = new Stamina();
        temperature = new Temperature();
    }

    public static String getSaveFileName(UUID uuid) {
        return uuid.toString() + ".player.json";
    }

    void onLoad(Player player) {
        this.player = player;
        this.display.onLoad(player);
        toEachPart((part) -> part.onLoad(this));
    }

    void onTick() {
        if (GameMode.ADVENTURE != player.getGameMode() && GameMode.SURVIVAL != player.getGameMode()) return;
        toEachPart(PhysicalPlayerPart::onTick);

        display.updateDisplay(thirst.getThirstPercentage(), stamina.getStaminaPercentage());
    }


    public void onDeath() {
        toEachPart(PhysicalPlayerPart::onDeath);
    }

    public void onLeave() {
        display.remove();
        PhysicalPlayerManager.remove(player.getUniqueId());
    }

    private void toEachPart(Consumer<PhysicalPlayerPart> toEach) {
        toEach.accept(this.stamina);
        toEach.accept(this.thirst);
        toEach.accept(this.temperature);
    }

    public void verifySprint() {
        if (!stamina.isLowSprint()) return;
        Player player = this.player.getPlayer();
        if (player != null)
            player.addPotionEffect(Stamina.LOW_SPRINT_EFFECT);
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

    public Temperature getTemperature() {
        return temperature;
    }

    public Player getPlayer() {
        return this.player;
    }
}
