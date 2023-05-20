package com.voltskiya.mechanics.food.listener;

import apple.mc.utilities.player.chat.SendMessage;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.food.config.CoolerItemConfig;
import com.voltskiya.mechanics.food.util.CoolerHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CoolerPlaceListener implements Listener, CoolerHandler {

    public CoolerPlaceListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    private static boolean isSameItemNearby(Location chestLoc, Material other) {
        World world = chestLoc.getWorld();
        int x = chestLoc.getBlockX();
        int y = chestLoc.getBlockY();
        int z = chestLoc.getBlockZ();
        return (isSameItemNearby(world, x + 1, y, z, other)
            || isSameItemNearby(world, x - 1, y, z, other)
            || isSameItemNearby(world, x, y, z + 1, other)
            || isSameItemNearby(world, x, y, z - 1, other));
    }

    private static boolean isSameItemNearby(World world, int x, int y, int z, Material other) {
        return world.getBlockAt(x + 1, y, z).getType() == other;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        CoolerItemConfig config = getCoolerConfig(item);
        if (config != null)
            coolerItem(event, config);
    }


    private void coolerItem(BlockPlaceEvent event, CoolerItemConfig config) {
        @NotNull Block placed = event.getBlockPlaced();
        if (isSameItemNearby(placed.getLocation(), placed.getType())) {
            SendMessage.get().red(event.getPlayer(), "You cannot place a cooler next to another chest.");
            event.setCancelled(true);
            return;
        }
        if (placed.getState() instanceof TileState state) {
            setBlockStateToCooler(state, config);
        }
    }
}
