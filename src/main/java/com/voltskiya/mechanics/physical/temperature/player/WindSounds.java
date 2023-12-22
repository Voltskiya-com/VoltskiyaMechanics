package com.voltskiya.mechanics.physical.temperature.player;

import com.voltskiya.mechanics.physical.temperature.config.visual.TemperatureVisualConfig;
import com.voltskiya.mechanics.physical.temperature.config.visual.WindVisualConfig;
import java.util.Random;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.sound.PlaySound;
import voltskiya.apple.utilities.sound.SoundAction;
import voltskiya.apple.utilities.sound.SoundManager;

public class WindSounds {

    private static final double CHANCE_OF_WIND = 1d;
    private static final double CHANCE_OF_LIGHT = 3;
    private static final double CHANCE_OF_HEAVY = 5;

    private final TemperatureVisual visual;
    private final Random random = new Random();
    private final SoundAction HEAVY = SoundAction.createDynamic("heavy", this::windSoundsHeavy);
    private final SoundAction LIGHT = SoundAction.createDynamic("light", this::windSoundsLight);
    private final SoundManager sounds = new SoundManager()
        .registerSound(this.HEAVY)
        .registerSound(this.LIGHT);
    private double wind = 0;
    private int tick = 0;

    public WindSounds(TemperatureVisual visual) {
        this.visual = visual;
    }

    private SoundAction windSoundsHeavy() {
        float pitch = random.nextFloat(0.5f);
        float volume = random.nextFloat(2f);
        return windSound(pitch, volume);
    }

    private SoundAction windSoundsLight() {
        float pitch = random.nextFloat();
        float volume = random.nextFloat();
        return windSound(pitch, volume);
    }

    @NotNull
    private PlaySound windSound(float pitch, float volume) {
        return SoundAction.create("wind", Sound.ITEM_ELYTRA_FLYING, SoundCategory.WEATHER, pitch, volume);
    }

    public void tick() {
        tick++;

        if (true) return;
        WindVisualConfig config = config();
        if (CHANCE_OF_HEAVY * wind > random.nextDouble()) {
            System.out.println("heavy");
            sounds.playSound(HEAVY.getName(), getPlayer());
        }
        if (CHANCE_OF_LIGHT * wind > random.nextDouble()) {
            System.out.println("light");
            sounds.playSound(LIGHT.getName(), getPlayer());
        }
    }

    private WindVisualConfig config() {
        return TemperatureVisualConfig.get().wind;
    }

    private Player getPlayer() {
        return visual.getPlayer();
    }

    public void setWind(double wind) {
        this.wind = wind;
    }
}
