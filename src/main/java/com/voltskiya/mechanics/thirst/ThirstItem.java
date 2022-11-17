package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.thirst.ThirstyPlayer;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import com.voltskiya.mechanics.thirst.config.ThirstConsumableConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ThirstItem {

    public static final NamespacedKey USES_COUNT_KEY = VoltskiyaPlugin.get()
        .namespacedKey("thirst.consumable.used_count");
    public static final NamespacedKey IS_DIRTY_KEY = VoltskiyaPlugin.get()
        .namespacedKey("thirst.consumable.is_dirty");

    public static void consumeUse(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        @Nullable ThirstConsumableConfig config = ThirstConfig.get().getConsumable(item);
        if (config == null)
            return;
        event.setCancelled(true);
        boolean didUse = decrementUsesCount(item);
        if (!didUse)
            return;
        boolean isDirty = getIsDirty(item);
        ThirstyPlayer.getPlayer(event.getPlayer()).drink(config.getDrinkAmount(), isDirty);
    }

    public static void setIsDirty(ItemStack item, boolean isDirty) {
        setKey(item, IS_DIRTY_KEY, PersistentDataType.BYTE, (byte) (isDirty ? 1 : 0));
    }

    public static boolean getIsDirty(ItemStack item) {
        byte isDirty = getKeyOrDefault(item, IS_DIRTY_KEY, PersistentDataType.BYTE, (byte) 0);
        return isDirty != 0;
    }

    public static void setUses(ItemStack item, int uses) {
        setKey(item, USES_COUNT_KEY, PersistentDataType.INTEGER, uses);
    }

    private static <T, Z> void setKey(ItemStack item, NamespacedKey key,
        PersistentDataType<T, Z> type,
        Z value) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, type, value);
        item.setItemMeta(itemMeta);
    }

    @NotNull

    private static <T, Z> Z getKeyOrDefault(ItemStack item, NamespacedKey key,
        PersistentDataType<T, Z> type, Z defaultVal) {
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(key, type, defaultVal);
    }

    private static boolean decrementUsesCount(ItemStack item) {
        int usedCount = getKeyOrDefault(item, USES_COUNT_KEY, PersistentDataType.INTEGER, 0);
        if (usedCount <= 0)
            return false;
        usedCount--;
        setKey(item, USES_COUNT_KEY, PersistentDataType.INTEGER, usedCount);
        return true;
    }
}
