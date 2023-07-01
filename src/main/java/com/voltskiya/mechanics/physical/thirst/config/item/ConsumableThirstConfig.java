package com.voltskiya.mechanics.physical.thirst.config.item;

import com.voltskiya.mechanics.physical.thirst.item.ThirstItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ConsumableThirstConfig {

    int getConsumeAmount();

    ThirstItem getThirstItem();

    int getUses();

    /**
     * @param item    the item to fill
     * @param isDirty true if the filled version should be dirty
     * @return an overflow item if {@link ItemStack#getAmount()} is greater than 1
     * @apiNote The item parameter may be modified to the filled item or the original item with amount of
     * ({@link ItemStack#getAmount()} - 1)
     */
    @Nullable
    default ItemStack fill(ItemStack item, boolean isDirty) {
        int amount = item.getAmount();
        if (amount == 1) {
            getThirstItem().toFull(item, isDirty);
            return null;
        }
        item.setAmount(amount - 1);
        return getThirstItem().toFull(isDirty);
    }

    /**
     * @param item the item to fill return nothing because the item parameter is always modified to be the empty version of itself
     */
    default void empty(ItemStack item) {
        getThirstItem().toEmpty(item);
    }
}