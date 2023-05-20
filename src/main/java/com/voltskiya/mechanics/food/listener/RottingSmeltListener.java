package com.voltskiya.mechanics.food.listener;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.food.config.CoolerConfig;
import com.voltskiya.mechanics.food.config.CoolerConfigDatabase;
import com.voltskiya.mechanics.food.util.FoodHandler;
import com.voltskiya.mechanics.food.util.FoodItem;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RottingSmeltListener implements Listener, FoodHandler {

    public RottingSmeltListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmeltEvent(FurnaceSmeltEvent event) {
        ItemStack resultItem = event.getResult();
        // see if both are edible and there is a furnace

        if (!foodConfig().hasFoodTimer(resultItem.getType())) return;
        if (!(event.getBlock().getState() instanceof Furnace furnace)) return;
        // both are edible and the same type
        // give the result decay
        ItemMeta resultMeta = resultItem.getItemMeta();
        if (resultMeta == null) {
            // im not dealing with this weird item
            return;
        }
        Inventory inventory = furnace.getInventory();
        ItemStack oldResultItem = inventory.getItem(2);
        CoolerConfig config = CoolerConfigDatabase.get().getDefaultConfig();
        FoodItem resultFood = new FoodItem(event.getResult(), config).update();
        if (oldResultItem == null || oldResultItem.getType().isAir()) {
            return;
        }
        FoodItem oldResultFood = new FoodItem(oldResultItem, config).update();
        resultFood.mergeIntoOther(oldResultFood);
        event.setResult(resultFood.getItem());
    }
}
