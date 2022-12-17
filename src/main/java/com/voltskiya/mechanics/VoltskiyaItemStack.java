package com.voltskiya.mechanics;

import lombok.AllArgsConstructor;
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

@AllArgsConstructor
public class VoltskiyaItemStack {
    private final ItemStack itemStack;

    public VoltskiyaItemStack(Material material) {
        itemStack = new ItemStack(material);
    }

    public VoltskiyaItemStack(Item item) {
        itemStack = item.toItemStack();
    }

    public Item getItem() {
        return Item.getItem(getItemStack());
    }

    public void changeTo(Item item, Player player) {
        if (1 == getItemStack().getAmount()) {
            getItemStack().setAmount(0);
            ItemStack newItem = item.toItemStack();
            VoltskiyaPlugin.get().scheduleSyncDelayedTask(() -> forceAdd(player, newItem));
        } else {
            getItemStack().setAmount(getItemStack().getAmount() - 1);
            forceAdd(player, item.toItemStack());
        }
    }

    public static void forceAdd(Player player, ItemStack itemStack) {
        Location location = player.getLocation();
        World world = location.getWorld();
        player.getInventory().addItem(new ItemStack[]{itemStack})
                .entrySet()
                .stream()
                .map((amountItemStackEntry) -> {
            amountItemStackEntry.getValue().setAmount(amountItemStackEntry.getKey());
            return amountItemStackEntry.getValue();
        }).forEach((itemStack1) -> world.dropItem(location, itemStack1));
    }

    protected <T, Z> void setKey(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, type, value);
        getItemStack().setItemMeta(itemMeta);
    }

    @NotNull
    protected <T, Z> Z getKeyOrDefault(NamespacedKey key, PersistentDataType<T, Z> type, Z def) {
        return getItemStack().getItemMeta().getPersistentDataContainer().getOrDefault(key, type, def);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static class BooleanType implements PersistentDataType<Byte, Boolean> {

        @NotNull
        public Class<Byte> getPrimitiveType() {
            return Byte.TYPE;
        }

        @NotNull
        public Class<Boolean> getComplexType() {
            return Boolean.TYPE;
        }

        @NotNull
        public Byte toPrimitive(@NotNull Boolean complex, @NotNull PersistentDataAdapterContext context) {
            return (byte) (complex ? 1 : 0);
        }

        @NotNull
        public Boolean fromPrimitive(@NotNull Byte primitive, @NotNull PersistentDataAdapterContext context) {
            return 1 == primitive;
        }
    }
}