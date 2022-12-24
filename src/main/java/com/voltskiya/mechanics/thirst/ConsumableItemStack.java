package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import com.voltskiya.mechanics.VoltskiyaPlayer;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public ItemStack consumeUse(Player player, @Nullable ItemStack replacement) {
        int consumeAmountByKey = getConsumeAmount();
        VoltskiyaPlayer voltskiyaPlayer = VoltskiyaPlayer.getPlayer(player);
        if (-1 != consumeAmountByKey) {
            voltskiyaPlayer.getThirst().drink(consumeAmountByKey, isDirty());
            return decreaseUsesCount(replacement);
        }
        Material material = getItemStack().getType();
        int consumeAmountByMaterial = ThirstConfig.getMaterialConsumeAmount(material);
        if (-1 != consumeAmountByMaterial)
            voltskiyaPlayer.getThirst().drink(consumeAmountByMaterial, false);
        return replacement;
    }

    private int getConsumeAmount() {
        return getKeyOrDefault(ThirstConfig.ConsumableItemConfig.CONSUME_AMOUNT_KEY, PersistentDataType.INTEGER, -1);
    }

    private int getUsesCount() {
        return getKeyOrDefault(ThirstConfig.ConsumableItemConfig.USES_KEY, PersistentDataType.INTEGER, -1);
    }

    public static ItemStack getWaterBottle() {
        ItemStack waterBottle = new ItemStack(Material.POTION);
        PotionMeta pmeta = (PotionMeta)waterBottle.getItemMeta();
        PotionData pdata = new PotionData(PotionType.WATER);
        pmeta.setBasePotionData(pdata);
        waterBottle.setItemMeta(pmeta);
        return waterBottle;
    }

    private boolean isDirty() {
        Item item = getItem();
        return Item.CANTEEN_DIRTY == item || Item.SIMPLE_BOTTLE_DIRTY == item || Item.BOTTLE_DIRTY == item;
    }

    private @Nullable ItemStack decreaseUsesCount(@Nullable ItemStack replacement) {
        int usesCount = getUsesCount();
        if (-1 == usesCount)
            return replacement;
        --usesCount;
        if (0 < usesCount) {
            setKey(ThirstConfig.ConsumableItemConfig.USES_KEY, PersistentDataType.INTEGER, usesCount);
            return getItemStack();
        }
        Item item = getItem();
        if (Item.CANTEEN_FULL == item || Item.CANTEEN_DIRTY == item)
            return Item.CANTEEN_EMPTY.toItemStack();
        if (Item.FILTERED_CANTEEN_FULL == item)
            return Item.FILTERED_CANTEEN_EMPTY.toItemStack();
        return Item.SIMPLE_BOTTLE_FULL != item && Item.SIMPLE_BOTTLE_DIRTY != item ? null : Item.SIMPLE_BOTTLE_EMPTY.toItemStack();
    }
}
