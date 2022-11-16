package com.voltskiya.mechanics;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Item {

    CANTEEN(Material.POTION, 0),
    SIMPLE_BOTTLE(Material.POTION, 1),
    FILTERED_CANTEEN(Material.POTION, 2);

    private final ItemStack itemStack;

    Item(@NotNull Material material, @Nullable Integer modelData) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(modelData);
        itemStack.setItemMeta(itemMeta);
        this.itemStack = itemStack;
    }

    public ItemStack getCopy() {
        return itemStack.clone();
    }
}
