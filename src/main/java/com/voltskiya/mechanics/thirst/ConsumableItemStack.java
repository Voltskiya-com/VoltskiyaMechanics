package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

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

    public boolean consumeUse(Player player) {
        Material material = itemStack.getType();
        if (material == Material.HONEY_BOTTLE || material == Material.MILK_BUCKET || isStandardPotion()) {
            ThirstyPlayer.getPlayer(player).drink(ThirstConfig.getConsumeAmount(material), false);
            return false;
        }
        if (material != Material.POTION)
            return false;
        if (getItem() == Item.BOTTLE_DIRTY) {
            ThirstyPlayer.getPlayer(player).drink(ThirstConfig.getConsumeAmount(material), true);
            return false;
        }
        if (!decreaseUsesCount())
            return false;
        int consumeAmount = getConsumeAmount();
        if (consumeAmount != -1) {
            ThirstyPlayer.getPlayer(player).drink(consumeAmount, isDirty());
            return true;
        }
        return false;
    }

    private int getConsumeAmount() {
        return getKeyOrDefault(CONSUME_AMOUNT_KEY, PersistentDataType.INTEGER, -1);
    }

    private boolean isDirty() {
        Item item = getItem();
        return item == Item.CANTEEN_DIRTY || item == Item.SIMPLE_BOTTLE_DIRTY || item == Item.BOTTLE_DIRTY;
    }

    private boolean isStandardPotion() {
        return itemStack.getType() == Material.POTION &&
                ((PotionMeta) itemStack.getItemMeta()).getBasePotionData().getType() != PotionType.UNCRAFTABLE;
    }

    private boolean decreaseUsesCount() {
        int usedCount = getUsedCount();
        if (usedCount == -1)
            return false;
        usedCount--;
        if (usedCount == 0) {
            Item item = getItem();
            if (item == Item.CANTEEN_FULL || item == Item.CANTEEN_DIRTY)
                Item.CANTEEN_EMPTY.set(itemStack);
            else if (item == Item.FILTERED_CANTEEN_FULL)
                Item.FILTERED_CANTEEN_EMPTY.set(itemStack);
            else if (item == Item.SIMPLE_BOTTLE_FULL || item == Item.SIMPLE_BOTTLE_DIRTY)
                Item.SIMPLE_BOTTLE_EMPTY.set(itemStack);
            return true;
        }
        setKey(USES_KEY, PersistentDataType.INTEGER, usedCount);
        return true;
    }

    private int getUsedCount() {
        return getKeyOrDefault(USES_KEY, PersistentDataType.INTEGER, -1);
    }
}
