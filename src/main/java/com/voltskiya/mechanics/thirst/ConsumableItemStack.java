package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.voltskiya.mechanics.thirst.config.ThirstConfig.ConsumableItemConfig.CONSUME_AMOUNT_KEY;
import static com.voltskiya.mechanics.thirst.config.ThirstConfig.ConsumableItemConfig.USES_KEY;

public class ConsumableItemStack extends VoltskiyaItemStack {
    public ConsumableItemStack(ItemStack itemStack) {
        super(itemStack);
    }

    public ConsumableItemStack(Material material) {
        super(material);
    }

    public ConsumableItemStack(Item item) {
        super(item);
    }

    public void consumeUse(Player player) {
        if (!isConsumable())
            return;
        if (!decreaseUsesCount())
            return;
        AtomicBoolean isDirty = new AtomicBoolean(false);
        Item item = getItem();
        isDirty.set(item == Item.CANTEEN_DIRTY || item == Item.SIMPLE_BOTTLE_DIRTY || item == Item.BOTTLE_DIRTY);
        ThirstyPlayer.getPlayer(player).drink(getKeyOrDefault(CONSUME_AMOUNT_KEY, PersistentDataType.INTEGER, 0), isDirty.get());
    }

    public boolean isConsumable() {
        return itemStack.getType() == Material.POTION || itemStack.getType() == Material.MILK_BUCKET;
    }

    private boolean decreaseUsesCount() {
        int usedCount = getKeyOrDefault(USES_KEY, PersistentDataType.INTEGER, 0);
        if (usedCount <= 0)
            return false;
        usedCount--;
        setKey(USES_KEY, PersistentDataType.INTEGER, usedCount);
        return true;
    }
}
