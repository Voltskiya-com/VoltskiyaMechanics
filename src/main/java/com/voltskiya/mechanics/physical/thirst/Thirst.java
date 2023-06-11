package com.voltskiya.mechanics.physical.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.thirst.config.ThirstConfig;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@Getter
public class Thirst {

    public static final int MAX_THIRST = 1000;
    public static final int MIN_THIRST = 0;

    private transient Player player;
    private int thirst = MAX_THIRST;
    private boolean isThirsty = true;

    public void onLoad(Player player) {
        this.player = player;
    }

    public void onTick() {
        if (!isThirsty)
            return;
        if (MIN_THIRST < thirst)
            thirst = Math.max(MIN_THIRST, thirst - ThirstConfig.get().getThirstRate());
        List<PotionEffect> effectsToAdd = ThirstConfig.get().getPotionEffects(thirst);
        if (!effectsToAdd.isEmpty())
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> player.addPotionEffects(effectsToAdd));
    }

    public void onDeath() {
        thirst = MAX_THIRST;
    }

    public boolean toggleIsThirsty() {
        return isThirsty = !isThirsty;
    }

    public double getThirstPercentage() {
        return thirst / (double) MAX_THIRST;
    }


    @Nullable
    public ItemStack onConsume(VoltskiyaItemStack itemStack, @Nullable ItemStack replacement) {
        Item item = itemStack.getItem();
        ThirstConfig.ConsumableItemConfig itemConfig = ThirstConfig.get(item);
        if (null == itemConfig) {
            drink(ThirstConfig.getMaterialConsumeAmount(itemStack.getItemStack().getType()));
            return replacement;
        }
        if (item.isDirty())
            player.addPotionEffects(ThirstConfig.get().getDirtyEffects());
        drink(itemConfig.getConsumeAmount());
        int usesCount = ThirstConfig.ConsumableItemConfig.getUses(itemStack);
        if (1 < usesCount) {
            ThirstConfig.ConsumableItemConfig.setUses(itemStack, usesCount - 1);
            return itemStack.getItemStack();
        }
        if (Item.CANTEEN_FULL == item || Item.CANTEEN_DIRTY == item)
            return Item.CANTEEN_EMPTY.toItemStack();
        if (Item.FILTERED_CANTEEN_FULL == item)
            return Item.FILTERED_CANTEEN_EMPTY.toItemStack();
        return Item.SIMPLE_BOTTLE_FULL == item || Item.SIMPLE_BOTTLE_DIRTY == item ? Item.SIMPLE_BOTTLE_EMPTY.toItemStack() : null;
    }

    public void drink(int consumeAmount) {
        thirst = Math.max(MIN_THIRST, Math.min(MAX_THIRST, thirst + consumeAmount));
    }

    public boolean shouldDisableSprint() {
        return 100 > thirst;//TODO add to config
    }
}
