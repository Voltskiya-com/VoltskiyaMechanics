package com.voltskiya.mechanics;

import java.util.Map;
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

    public static void forceAdd(Player player, ItemStack itemStack) {
        Location location = player.getLocation();
        World world = location.getWorld();
        player.getInventory().addItem(new ItemStack[]{itemStack})
            .entrySet()
            .stream()
            .map(VoltskiyaItemStack::setAmount)
            .forEach((itemStack1) -> world.dropItem(location, itemStack1));
    }

    private static ItemStack setAmount(Map.Entry<Integer, ? extends ItemStack> itemStack) {
        itemStack.getValue().setAmount(itemStack.getKey());
        return itemStack.getValue();
    }

    public final Item getItem() {
        return Item.getItem(getItemStack());
    }

    public void change1To(Item item, Player player) {
        getItemStack().setAmount(getItemStack().getAmount() - 1);
        forceAdd(player, item.toItemStack());
    }

    public <T, Z> void setKey(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, type, value);
        getItemStack().setItemMeta(itemMeta);
    }

    @NotNull
    public <T, Z> Z getKeyOrDefault(NamespacedKey key, PersistentDataType<T, Z> type, Z def) {
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