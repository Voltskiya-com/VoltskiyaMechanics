package com.voltskiya.mechanics.thirst.config;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class ThirstConsumableConfig {

    public static final NamespacedKey THIRST_CONSUMALBE_KEY = VoltskiyaPlugin.get()
        .namespacedKey("thirst.consumable");

    private final int uses = 1;
    private final String key = "unknown";
    private final int drinkAmount = 300;

    private final ThirstConsumableVariant itemEmpty = new ThirstConsumableVariant();
    private final ThirstConsumableVariant itemFullClean = new ThirstConsumableVariant();
    private final ThirstConsumableVariant itemFullDirty = new ThirstConsumableVariant();
    private transient boolean isInitialized = false;

    private void verifyItems() {
        if (isInitialized)
            return;
        this.isInitialized = true;
        itemEmpty.setKey(key);
        itemFullClean.setKey(key);
        itemFullDirty.setKey(key);

        itemFullClean.setFull(this.uses);
        itemFullDirty.setFull(this.uses);
        itemFullDirty.setDirty();
    }

    public ItemStack getItemFullDirty() {
        this.verifyItems();
        return this.itemFullDirty.getCopy();
    }

    public ItemStack getItemFullClean() {
        this.verifyItems();
        return this.itemFullClean.getCopy();
    }

    public ItemStack getItemEmpty() {
        this.verifyItems();
        return this.itemEmpty.getCopy();
    }


    public int getDrinkAmount() {
        return this.drinkAmount;
    }

    public int getUses() {
        return uses;
    }

    public String getKey() {
        return key;
    }
}
