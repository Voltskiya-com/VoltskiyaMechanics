package com.voltskiya.mechanics;

import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum Item {
    UNKNOWN(Material.AIR, new ItemConfig(-1, Component.empty(), Collections.emptyList()) {
        @Override
        public Tag[] getTags() {
            return new Tag[0];
        }
    }),

    CANTEEN_EMPTY(Material.POTION, ThirstConfig.get("canteen_empty")),
    CANTEEN_FULL(Material.POTION, ThirstConfig.get("canteen_full")),
    CANTEEN_DIRTY(Material.POTION, ThirstConfig.get("canteen_dirty")),
    SIMPLE_BOTTLE_EMPTY(Material.POTION, ThirstConfig.get("simple_bottle_empty")),
    SIMPLE_BOTTLE_FULL(Material.POTION, ThirstConfig.get("simple_bottle_full")),
    SIMPLE_BOTTLE_DIRTY(Material.POTION, ThirstConfig.get("simple_bottle_dirty")),
    FILTERED_CANTEEN_EMPTY(Material.POTION, ThirstConfig.get("filtered_canteen_empty")),
    FILTERED_CANTEEN_FULL(Material.POTION, ThirstConfig.get("filtered_canteen_full")),
    BOTTLE_DIRTY(Material.POTION, ThirstConfig.get("bottle_dirty"))
    ;

    private final Material material;
    private final int texture;
    private final Tag[] tags;
    private final Component name;
    private final List<Component> lore;


    Item(Material material, ItemConfig config) {
        this.material = material;
        texture = config.getTexture();
        this.tags = config.getTags();
        name = config.getName();
        lore = config.getLore();
    }

    public static Item getItem(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return UNKNOWN;
        ItemMeta itemMeta = itemStack.getItemMeta();
        var container = itemMeta.getPersistentDataContainer();
        return container.getOrDefault(ITEM_KEY, ItemType.ITEM, UNKNOWN);
    }

    public boolean is(ItemStack itemStack) {
        return getItem(itemStack) == this;
    }

    private static final NamespacedKey ITEM_KEY = VoltskiyaPlugin.get().namespacedKey("item_name");
    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(texture);
        itemMeta.displayName(name);
        var container = itemMeta.getPersistentDataContainer();
        container.set(ITEM_KEY, ItemType.ITEM, this);
        itemMeta.lore(lore);
        Arrays.stream(tags).forEach(tag -> tag.set(container));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public static class Tag {
        private final NamespacedKey key;
        private final Object value;
        private final PersistentDataType type;


        public Tag(NamespacedKey key, int value) {
            this.key = key;
            this.value = value;
            type = PersistentDataType.INTEGER;
        }

        private void set(PersistentDataContainer container) {
            container.set(key, type, value);
        }
    }

    private static class ItemType implements PersistentDataType<String, Item>{
        private static final ItemType ITEM = new ItemType();
        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<Item> getComplexType() {
            return Item.class;
        }

        @Override
        public @NotNull String toPrimitive(@NotNull Item complex, @NotNull PersistentDataAdapterContext context) {
            return complex.name();
        }

        @Override
        public @NotNull Item fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
            return Item.valueOf(primitive);
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static abstract class ItemConfig {
        private final int texture;
        private final Component name;
        private final List<Component> lore;
        public abstract Tag[] getTags();

    }
}
