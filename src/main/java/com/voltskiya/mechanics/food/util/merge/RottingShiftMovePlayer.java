package com.voltskiya.mechanics.food.util.merge;

import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RottingShiftMovePlayer extends RottingShiftMove {

    protected final PlayerInventory playerInventory;
    protected final SlotType slotType;
    private final InventoryView view;

    public RottingShiftMovePlayer(PlayerInventory destination, InventoryView view, ItemStack source, SlotType slotType) {
        super(destination, source, false);
        this.playerInventory = destination;
        this.view = view;
        this.slotType = slotType;
    }

    @Override
    protected boolean shouldSkipSlot(int slot) {
        SlotType viewSlotType = view.getSlotType(slot);
        return viewSlotType == slotType &&
            viewSlotType != SlotType.ARMOR &&
            viewSlotType != SlotType.RESULT;
    }
}
