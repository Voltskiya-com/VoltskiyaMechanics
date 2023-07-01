package com.voltskiya.mechanics.physical.thirst.item;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ThirstKeys {

    private static final NamespacedKey THIRST_ITEM_TYPE_KEY = VoltskiyaPlugin.get().namespacedKey("thirst.item_type");
    private static final NamespacedKey THIRST_DIRTY_KEY = VoltskiyaPlugin.get().namespacedKey("thirst.dirty");
    private static final NamespacedKey USES_KEY = VoltskiyaPlugin.get().namespacedKey("thirst.consumable.used_count");

    @NotNull
    private static PersistentDataContainer container(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer();
    }

    public static boolean isDirty(ItemStack item) {
        return container(item).getOrDefault(THIRST_DIRTY_KEY, PersistentDataType.BOOLEAN, false);
    }

    public static void setDirty(ItemStack item, boolean isDirty) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(THIRST_DIRTY_KEY, PersistentDataType.BOOLEAN, isDirty);
        item.setItemMeta(meta);
    }

    public static int getUses(ItemStack item) {
        return container(item).getOrDefault(USES_KEY, PersistentDataType.INTEGER, 0);
    }

    public static void setUses(ItemStack item, int uses) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(USES_KEY, PersistentDataType.INTEGER, uses);
        item.setItemMeta(meta);
    }

    public static String getItemType(ItemStack item) {
        return container(item).get(THIRST_ITEM_TYPE_KEY, PersistentDataType.STRING);
    }

    public static void setItemType(ItemStack item, String itemType) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(THIRST_ITEM_TYPE_KEY, PersistentDataType.STRING, itemType);
        item.setItemMeta(meta);
    }


    public static void setAll(ItemStack item, String itemType, int usesLeft, boolean isDirty) {
        ThirstKeys.setItemType(item, itemType);
        ThirstKeys.setUses(item, usesLeft);
        ThirstKeys.setDirty(item, isDirty);
    }
}
