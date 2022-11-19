package com.voltskiya.mechanics;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static com.voltskiya.mechanics.thirst.config.ThirstConfig.ConsumableItemConfig.USES_KEY;

public class VoltskiyaItemStack {

    @Getter
    protected final ItemStack itemStack;

    public VoltskiyaItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public VoltskiyaItemStack(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public VoltskiyaItemStack(Item item) {
        itemStack = item.toItemStack();
    }

    public Item getItem() {
        return Item.getItem(itemStack);
    }

    public void changeTo(Item item, Player player) {
        if (itemStack.getAmount() == 1)
            Item.BOTTLE_DIRTY.set(itemStack);
        else {
            itemStack.setAmount(itemStack.getAmount() - 1);
            forceAdd(player, item.toItemStack());
        }
    }

    public static void forceAdd(Player player, ItemStack itemStack) {
        Location location = player.getLocation();
        World world = location.getWorld();
        player.getInventory().addItem(itemStack)
                .entrySet()
                .stream()
                .map(amountItemStackEntry ->  {
                    amountItemStackEntry.getValue().setAmount(amountItemStackEntry.getKey());
                    return amountItemStackEntry.getValue();
                })
                .forEach(itemStack1 -> world.dropItem(location, itemStack1));
    }

    protected <T, Z> void setKey(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, type, value);
        itemStack.setItemMeta(itemMeta);
    }

    @NotNull
    protected <T, Z> Z getKeyOrDefault(NamespacedKey key, PersistentDataType<T, Z> type, Z def) {
        return itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(key, type, def);
    }

    public static class BooleanType implements PersistentDataType<Byte, Boolean> {

        @Override
        public @NotNull Class<Byte> getPrimitiveType() {
            return byte.class;
        }

        @Override
        public @NotNull Class<Boolean> getComplexType() {
            return boolean.class;
        }

        @Override
        public @NotNull Byte toPrimitive(@NotNull Boolean complex, @NotNull PersistentDataAdapterContext context) {
            return complex ? (byte)1 :(byte)0;
        }

        @Override
        public @NotNull Boolean fromPrimitive(@NotNull Byte primitive, @NotNull PersistentDataAdapterContext context) {
            return primitive == 1;
        }
    }
}
