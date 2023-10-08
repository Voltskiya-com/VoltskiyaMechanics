package com.voltskiya.mechanics.physical.temperature;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.minecraft.player.PlayerUtils;

public class TemperatureVisual {

    private final Player player;
    private final BukkitTask task;
    private final Random random = new Random();
    private int freezeTicks;
    private double wetAmount;

    public TemperatureVisual(Player player) {
        this.player = player;
        task = Bukkit.getScheduler().runTaskTimer(VoltskiyaPlugin.get(), this::tick, 1, 1);
    }

    private void tick() {
        if (!player.isOnline()) {
            task.cancel();
            return;
        }
        if (!PlayerUtils.isSurvival(player)) return;
        if (this.freezeTicks > 0) {
            freeze();
        } else {
        }
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

    private Vector normalVector(double x1, double z1, double y1, boolean isZeroY, boolean isZeroZ) {
        double range = .25;
        double x2 = random.nextDouble(-range, range);
        double randomVal = random.nextDouble(-range, range);
        double y2;
        double z2;
        if (isZeroY) {
            z2 = randomVal;
            y2 = -(x1 * x2 + z1 * z2) / y1;
        } else {
            y2 = randomVal;
            z2 = -(x1 * x2 + y1 * y2) / z1;
        }
        return new Vector(x1 + x2, y1 + y2, z1 + z2);
    }

    public TemperatureVisual freezeTicks(int freezeTicks) {
        this.freezeTicks = freezeTicks;
        return this;
    }

    public TemperatureVisual wetAmount(double wetAmount) {
        this.wetAmount = wetAmount;
        return this;
    }
}
