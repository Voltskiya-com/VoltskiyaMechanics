package com.voltskiya.mechanics;

import com.voltskiya.mechanics.thirst.ThirstyPlayer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.voltskiya.mechanics.thirst.config.ThirstConfig.ConsumableItemConfig.CONSUME_AMOUNT_KEY;
import static com.voltskiya.mechanics.thirst.config.ThirstConfig.ConsumableItemConfig.USES_KEY;

public class VoltskiyaItemStack {

    @Getter
    private final ItemStack itemStack;

    public VoltskiyaItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public VoltskiyaItemStack(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public VoltskiyaItemStack(Item item) {
        itemStack = item.toItemStack();
    }

    public Optional<Item> getItem() {
        return Item.getItem(itemStack);
    }

    public void consumeUse(Player player) {
        if (!decreaseUsesCount())
            return;
        AtomicBoolean isDirty = new AtomicBoolean(false);
        getItem().ifPresent(item -> isDirty.set(item == Item.CANTEEN_DIRTY || item == Item.SIMPLE_BOTTLE_DIRTY));
        ThirstyPlayer.getPlayer(player).drink(getKeyOrDefault(CONSUME_AMOUNT_KEY, PersistentDataType.INTEGER, 0), isDirty.get());
    }

    private boolean decreaseUsesCount() {
        int usedCount = getKeyOrDefault(USES_KEY, PersistentDataType.INTEGER, 0);
        if (usedCount <= 0)
            return false;
        usedCount--;
        setKey(USES_KEY, PersistentDataType.INTEGER, usedCount);
        return true;
    }

    public void setBoolean(NamespacedKey key, boolean bool) {
        setKey(key, PersistentDataType.BYTE, (byte) (bool ? 1 : 0));
    }

    public boolean getBoolean(NamespacedKey key) {
        return getKeyOrDefault(key, PersistentDataType.BYTE, (byte) 0) != 0;
    }

    private <T, Z> void setKey(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, type, value);
        itemStack.setItemMeta(itemMeta);
    }

    @NotNull
    private <T, Z> Z getKeyOrDefault(NamespacedKey key, PersistentDataType<T, Z> type, Z def) {
        return itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(key, type, def);
    }
}
