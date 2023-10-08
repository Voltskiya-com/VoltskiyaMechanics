package com.voltskiya.mechanics.physical.temperature.check;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.utilities.util.NumberUtils;
import com.voltskiya.mechanics.physical.temperature.TemperatureCalc;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts.BlockSourcesConsts;
import com.voltskiya.mechanics.physical.temperature.check.TemperatureConsts.WetnessConsts;
import com.voltskiya.mechanics.physical.temperature.config.blocks.TemperatureBlock;
import com.voltskiya.mechanics.physical.temperature.config.blocks.TemperatureBlocksConfig;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.chance.ChanceShapes;

public class TemperatureChecks {

    private static final int INSIDE_RAY_LENGTH = 40;
    private static final int INSIDE_RAY_COUNT = 10; // can be low because we do a moving average

    /**
     * @return [0, 1] high insideness means you're in an enclosed area
     */
    public static double insideness(Location center) {
        ChanceShapes chance = new ChanceShapes();
        World world = center.getWorld();
        Vector centerVector = center.toVector();

        double distanceSum = 0;
        for (int i = 0; i < INSIDE_RAY_COUNT; i++) {
            Vector randomDirection = chance.sphere(1);
            RayTraceResult rayTrace = world.rayTraceBlocks(center, randomDirection, INSIDE_RAY_LENGTH,
                FluidCollisionMode.ALWAYS, false);
            if (rayTrace == null) distanceSum += INSIDE_RAY_LENGTH;
            else distanceSum += centerVector.distance(rayTrace.getHitPosition());
        }
        double insideness = 1 - distanceSum / INSIDE_RAY_COUNT / INSIDE_RAY_LENGTH;

        // inflate insideness because the ground is almost always below you, which deflates insideness
        // range is still [0 to 1)
        double inflated = 2 * insideness - insideness * insideness;
        return Math.pow(inflated, 10); // deflate low values
    }

    /**
     * (-inf, inf)
     *
     * @return a temperature value considering all heat sources
     */
    public static double blockSources(TemperatureCalc calc) {
        Location center = calc.getPlayer().getLocation();
        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();
        final World world = center.getWorld();
        double total = 0;
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();
        int checkRadius = blockSources().checkRadius;
        for (int xi = -checkRadius; xi <= checkRadius; xi++) {
            for (int zi = -checkRadius; zi <= checkRadius; zi++) {
                for (int yi = -checkRadius; yi <= checkRadius; yi++) {
                    if (!NumberUtils.between(minY, y + yi, maxY)) continue;
                    @NotNull Block block = world.getBlockAt(x + xi, y + yi, z + zi);
                    @Nullable TemperatureBlock temp = TemperatureBlocksConfig.get().getBlock(block.getType());
                    if (temp != null) {
                        double distance = VectorUtils.magnitude(xi, yi, zi);
                        double blockTemp = temp.getTemperature();
                        total += blockTemp * Math.pow(blockSources().distanceImpact, -distance);
                    }
                }
            }
        }
        double heatSources = Math.pow(Math.abs(total), 1 / 2f);
        if (total < 0)
            heatSources = -heatSources;

        double insidenessMultiplied = blockSources().insidenessMultiplier * calc.getInsideness();
        return (blockSources().rawPercImportance + insidenessMultiplied) * heatSources;
    }


    /**
     * @return range:[0-100]
     */
    public static double wetness(TemperatureCalc calc) {
        Player player = calc.getPlayer();
        double maxVal = wetness().maxWetness;

        double wetProtection = calc.getClothing().getWetProtection();
        double heavyWetProtection = Math.max(1, wetProtection - 100 * .5); // 100 is max wetProtection
        if (player.isUnderWater()) return maxVal / heavyWetProtection;
        if (player.isInWater()) return maxVal * .55 / heavyWetProtection;
        if (player.isInPowderedSnow()) return maxVal * .45 / wetProtection;
        if (player.isInRain()) return maxVal * .40 / wetProtection;
        return 0;
    }

    private static WetnessConsts wetness() {
        return consts().wetness;
    }

    private static BlockSourcesConsts blockSources() {
        return consts().blockSources;
    }

    private static TemperatureConsts consts() {
        return TemperatureConsts.get();
    }
}
