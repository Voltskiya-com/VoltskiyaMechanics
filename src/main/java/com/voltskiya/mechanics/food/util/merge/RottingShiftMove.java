package com.voltskiya.mechanics.food.util.merge;

import com.voltskiya.mechanics.food.config.CoolerConfig;
import com.voltskiya.mechanics.food.util.CoolerHandler;
import com.voltskiya.mechanics.food.util.FoodHandler;
import com.voltskiya.mechanics.food.util.FoodItem;
import com.voltskiya.mechanics.rotting.ContainerRottingDecrement;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RottingShiftMove implements FoodHandler, CoolerHandler {

    private final Inventory destination;
    private final FoodItem source;
    private final CoolerConfig coolerConfig;
    private Integer emptySlot = null;

    public RottingShiftMove(Inventory destination, ItemStack source, boolean updateItemsFirst) {
        this.destination = destination;
        this.coolerConfig = getCoolerConfigOrFallback(destination);
        this.source = new FoodItem(source, coolerConfig);

        // verify all items are up to date
        if (!updateItemsFirst) return;
        this.source.update();
        new ContainerRottingDecrement(destination).tick();
    }

    public RottingShiftMoveResult complete() {
        // if this is a food that we should worry about
        if (!foodConfig().hasFoodTimer(source.getItem().getType())) return RottingShiftMoveResult.NO_ACTION;

//        boolean isFurnace = destination instanceof FurnaceInventory;
//        boolean isBrewingStand = destination instanceof BrewingStand;
//        boolean isCraftingTable = destination instanceof CraftingInventory;
        ItemStack[] contents = destination.getContents();
        for (int i = 0; i < contents.length; i++) {
//            if (isFurnace) {
//                if (i == 2) continue;
//            } else if (isBrewingStand) {
//                if (i != 3) continue;
//            } else if (isCraftingTable) {
//                if (i == 0) continue;
//            }
            if (shouldSkipSlot(i)) continue;
            if (contents[i] == null || contents[i].getType().isAir()) {
                emptySlot = i;
                continue;
            }
            FoodItem content = new FoodItem(contents[i], coolerConfig);
            this.source.mergeIntoOther(content);
            if (this.source.getItem().getAmount() == 0)
                return RottingShiftMoveResult.COMPLETE;
        }
        // if there is anything left, put it in an empty slot
        if (emptySlot == null)
            return RottingShiftMoveResult.PARTIAL;

        destination.setItem(emptySlot, new ItemStack(source.getItem()));
        source.getItem().setAmount(0);
        return RottingShiftMoveResult.COMPLETE;
    }

    protected boolean shouldSkipSlot(int slot) {
        return false;
    }

}
