package com.voltskiya.mechanics;

import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import com.voltskiya.mechanics.thirst.config.ThirstConsumableConfig;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Item {

    CANTEEN("canteen"),
    SIMPLE_BOTTLE("simple_bottle"),
    FILTERED_CANTEEN("filtered_canteen");
    @NotNull
    private final ThirstConsumableConfig itemStack;

    private final String id;

    Item(String id) {
        @Nullable ThirstConsumableConfig item;
        this.id = id;
        item = ThirstConfig.get().getConsumable(this.id);
        if (item == null)
            item = ThirstConfig.get().createConsumable(this.id);
        itemStack = item;
    }

    public ItemStack getCopy() {
        return itemStack.getItemEmpty();
    }
}
