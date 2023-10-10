package com.voltskiya.mechanics.physical.temperature.player;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import voltskiya.apple.utilities.minecraft.player.PlayerUtils;
import voltskiya.apple.utilities.sound.SoundAction;

public class TemperatureVisual {

    private static final SoundAction WIND1 = SoundAction.create("wind1", Sound.ENTITY_PHANTOM_FLAP, SoundCategory.WEATHER, 0.5f, 1);
    private final Player player;
    private final BukkitTask task;
    private final Random random = new Random();
    private final WindSounds wind;
    private int freezeTicks;
    private double wetAmount;

    public TemperatureVisual(Player player) {
        this.player = player;
        this.task = Bukkit.getScheduler().runTaskTimer(VoltskiyaPlugin.get(), this::tick, 1, 1);
        this.wind = new WindSounds(this);
    }

    private void tick() {
        if (!player.isOnline()) {
            task.cancel();
            return;
        }
        if (!PlayerUtils.isSurvival(player)) return;

        if (this.freezeTicks > 0) freeze();
        wind.tick();
        sweat();
    }


    private void freeze() {
        int alreadyFreezing = player.getFreezeTicks();
        if (alreadyFreezing >= this.freezeTicks) return;
        player.setFreezeTicks(this.freezeTicks);
    }

    private void sweat() {
        boolean skipSweat = this.wetAmount < random.nextDouble();
        if (skipSweat) return;

        BoundingBox hitBox = player.getBoundingBox();
        double x = random.nextDouble(hitBox.getWidthX());
        double y = random.nextDouble(hitBox.getHeight());
        double z = random.nextDouble(hitBox.getWidthZ());
        Location location = hitBox.getMin().toLocation(player.getWorld()).add(x, y, z);

        double count = this.wetAmount / random.nextDouble(0.1, 1);
        Particle.FALLING_DRIPSTONE_WATER.builder()
            .location(location)
            .count((int) count + 1)
            .spawn();
    }

    public TemperatureVisual freezeTicks(int freezeTicks) {
        this.freezeTicks = freezeTicks;
        return this;
    }

    public TemperatureVisual wetAmount(double wetAmount) {
        this.wetAmount = wetAmount;
        return this;
    }

    public TemperatureVisual setWind(double windChance) {
        this.wind.setWind(windChance);
        return this;
    }

    public Player getPlayer() {
        return this.player;
    }
}
