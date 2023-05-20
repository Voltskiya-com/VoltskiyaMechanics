package com.voltskiya.mechanics.food.util.merge;

import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface RottingMerge {

    static RottingShiftMoveResult shiftMove(Inventory destination, ItemStack source, boolean updateItemsFirst) {
        return new RottingShiftMove(destination, source, updateItemsFirst).complete();
    }

    static RottingShiftMoveResult shiftMove(Inventory destination, ItemStack source) {
        return shiftMove(destination, source, true);
    }

    static RottingShiftMoveResult playerShiftMove(PlayerInventory destination, InventoryView view, SlotType slotType,
        ItemStack source) {
        return new RottingShiftMovePlayer(destination, view, source, slotType).complete();
    }
}
