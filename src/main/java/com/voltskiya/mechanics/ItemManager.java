package com.voltskiya.mechanics;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemManager {

    public static final Item CANTEEN = new Item(Material.POTION, 0);
    public static final Item SIMPLE_BOTTLE = new Item(Material.POTION, 1);
    public static final Item FILTERED_CANTEEN = new Item(Material.POTION, 2);

    public static class Item {
        private final ItemStack itemStack;

        private Item(@NotNull Material material, @Nullable Integer modelData) {
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
}
