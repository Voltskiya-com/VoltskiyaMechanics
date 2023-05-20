package com.voltskiya.mechanics.food.util;

import com.voltskiya.mechanics.food.config.CoolerConfig;
import com.voltskiya.mechanics.food.config.FoodRotConfig;
import com.voltskiya.mechanics.food.config.FoodRotItemConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoodItem implements FoodHandler {

    private final ItemMeta itemMeta;

    private final FoodRotItemConfig itemConfig;
    @Nullable
    private final CoolerConfig containerConfig;
    private final ItemStack item;
    private ItemStack rotInto;


    public FoodItem(ItemStack item, @Nullable CoolerConfig containerConfig) {
        this.item = item;
        this.itemMeta = item.getItemMeta();
        this.itemConfig = FoodRotConfig.get().getFoodTimer(item.getType());
        this.containerConfig = containerConfig;
    }

    private double computeDecayAmount() {
        if (this.itemConfig == null) return -1;
        PersistentDataContainer dataContainer = getDataContainer();
        long now = getTime();
        long lastUpdated = dataContainer.getOrDefault(DECAY_LAST_UPDATED_TIMESTAMP_KEY, PersistentDataType.LONG, now);
        double decayAmount = getDecayAmount();
        dataContainer.set(DECAY_LAST_UPDATED_TIMESTAMP_KEY, PersistentDataType.LONG, now);
        if (containerConfig == null) return decayAmount;
        return decayAmount + (now - lastUpdated) * containerConfig.getRotMultiplier();
    }

    private double getDecayAmount() {
        return getDataContainer().getOrDefault(DECAY_AMOUNT_KEY, PersistentDataType.DOUBLE, 0d);
    }

    private void setDecayAmount(double newDecayAmount) {
        getDataContainer().set(DECAY_AMOUNT_KEY, PersistentDataType.DOUBLE, newDecayAmount);
    }

    @NotNull
    private PersistentDataContainer getDataContainer() {
        return itemMeta.getPersistentDataContainer();
    }

    public FoodItem update() {
        if (itemConfig == null) return this;

        double newDecayAmount = (int) computeDecayAmount();
        int maxDecay = itemConfig.getTicksToDecay();
        setDecayAmount(newDecayAmount % maxDecay);
        if (newDecayAmount >= maxDecay) {
            rotItem((int) (newDecayAmount / maxDecay));
            if (rotInto != null) new FoodItem(rotInto, containerConfig).update();
        }
        itemMeta.lore(FoodItemLore.makeLore(maxDecay - (int) (newDecayAmount % maxDecay)));
        item.setItemMeta(itemMeta);
        return this;
    }

    private void rotItem(int decayCycles) {
        int newAmount = item.getAmount() - decayCycles;
        Material rotIntoType = itemConfig.getRotInto();
        if (newAmount <= 0) {
            if (rotIntoType != null)
                rotInto = new ItemStack(rotIntoType, item.getAmount());
            item.setAmount(0);
            return;
        }
        if (rotIntoType != null)
            rotInto = new ItemStack(rotIntoType, decayCycles);
        item.setAmount(newAmount);
    }

    public void mergeIntoOther(FoodItem other) {
        // check these items are the same type
        if (this.item.getType() != other.item.getType()) return;
        
        int myAmount = this.item.getAmount();
        int otherAmount = other.item.getAmount();

        // check we can add more items to this slot
        int leftToStack = other.item.getMaxStackSize() - otherAmount;
        if (leftToStack == 0) return;
        int moveAmount = Math.min(leftToStack, myAmount);
        double myDecay = moveAmount * this.getDecayAmount();
        double otherDecay = otherAmount * other.getDecayAmount();
        double newDecayAmount = (myDecay + otherDecay) / (moveAmount + otherAmount);
        other.item.setAmount(moveAmount + otherAmount);
        other.setDecayAmount(newDecayAmount);
        this.item.setAmount(myAmount - moveAmount);
    }

    public ItemStack getRot() {
        return this.rotInto;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
