package com.voltskiya.mechanics;

import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public enum Item {
    UNKNOWN(Material.AIR, new Item.ItemConfig(-1, Component.empty(), Collections.emptyList()) {
        public Item.Tag<?, ?>[] getTags() {
            return new Item.Tag[0];
        }
    }),
    CANTEEN_EMPTY(Material.GLASS_BOTTLE, ThirstConfig.compute("canteen_empty", false, 0)),
    CANTEEN_FULL(Material.POTION, ThirstConfig.compute("canteen_full", false, 4)),
    CANTEEN_DIRTY(Material.POTION, ThirstConfig.compute("canteen_dirty", true, 4)),
    SIMPLE_BOTTLE_EMPTY(Material.GLASS_BOTTLE, ThirstConfig.compute("simple_bottle_empty", false, 1)),
    SIMPLE_BOTTLE_FULL(Material.POTION, ThirstConfig.compute("simple_bottle_full", false, 1)),
    SIMPLE_BOTTLE_DIRTY(Material.POTION, ThirstConfig.compute("simple_bottle_dirty", true, 1)),
    FILTERED_CANTEEN_EMPTY(Material.GLASS_BOTTLE, ThirstConfig.compute("filtered_canteen_empty", false, 4)),
    FILTERED_CANTEEN_FULL(Material.POTION, ThirstConfig.compute("filtered_canteen_full", false, 4)),
    BOTTLE_DIRTY(Material.POTION, ThirstConfig.compute("bottle_dirty", true, 1));

    private final Material material;
    private final int texture;
    private final Item.Tag<?, ?>[] tags;
    private final Component displayName;
    private final List<Component> lore;
    private static final NamespacedKey ITEM_KEY = VoltskiyaPlugin.get().namespacedKey("item_name");

    Item(Material material, Item.ItemConfig config) {
        this.material = material;
        texture = config.getTexture();
        tags = config.getTags();
        displayName = config.getName();
        lore = config.getLore();
    }

    public static Item getItem(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return UNKNOWN;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        try {
            String name = container.getOrDefault(ITEM_KEY, PersistentDataType.STRING, "");
            return valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return UNKNOWN;
        }
    }

    public boolean is(ItemStack itemStack) {
        return getItem(itemStack) == this;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material);
        setItemMeta(itemStack);
        return itemStack;
    }

    public void set(ItemStack itemStack) {
        itemStack.setType(material);
        itemStack.setAmount(1);
        setItemMeta(itemStack);
    }

    private void setItemMeta(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(texture);
        itemMeta.displayName(displayName);
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(ITEM_KEY, PersistentDataType.STRING, name());
        itemMeta.lore(lore);
        Arrays.stream(tags).forEach(tag -> tag.set(container));
        itemStack.setItemMeta(itemMeta);
    }

    @AllArgsConstructor
    @Getter
    public abstract static class ItemConfig {
        private final int texture;
        private final Component name;
        private final List<Component> lore;

        public abstract Item.Tag<?, ?>[] getTags();
    }

    @AllArgsConstructor
    public static class Tag<T, Z> {
        private final NamespacedKey key;
        private final PersistentDataType<T, ? super Z> type;
        private final Z value;

        private void set(PersistentDataContainer container) {
            container.set(key, type, value);
        }
    }

    private static class ItemType implements PersistentDataType<String, Item> {
        private static final Item.ItemType ITEM = new Item.ItemType();

        @NotNull
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @NotNull
        public Class<Item> getComplexType() {
            return Item.class;
        }

        @NotNull
        public String toPrimitive(@NotNull Item complex, @NotNull PersistentDataAdapterContext context) {
            return complex.name();
        }

        @NotNull
        public Item fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
            return valueOf(primitive);
        }
    }
}