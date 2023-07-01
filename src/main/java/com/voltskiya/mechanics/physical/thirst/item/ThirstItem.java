package com.voltskiya.mechanics.physical.thirst.item;

import com.voltskiya.mechanics.physical.PhysicalModule;
import com.voltskiya.mechanics.physical.thirst.config.ThirstConfig;
import com.voltskiya.mechanics.physical.thirst.config.item.ExactThirstConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ThirstItem {
    NORMAL_BOTTLE(0, 0x7fc1d4, "normal_bottle", "Water Bottle"),
    SIMPLE_BOTTLE(1, 0x7fc1d4, "simple_bottle", "Simple Bottle"),
    CANTEEN(2, 0x7fa2d4, "canteen", "Canteen"),
    FILTERED_CANTEEN(4, 0x4668bd, "filtered_canteen", "Filtered Canteen");

    public static final int DIRTY_COLOR = 0x988585;
    private static final Map<String, ThirstItem> idToItem = Arrays.stream(values())
        .collect(Collectors.toMap(item -> item.id, item -> item));
    private static final TextComponent DIRTY_DISPLAY = Component.text("Dirty", TextColor.color(DIRTY_COLOR));
    private final int texture;
    private final int color;
    private final String id;
    private final String name;
    private ExactThirstConfig config;

    ThirstItem(int texture, int color, String id, String name) {
        this.texture = texture;
        this.color = color;
        this.id = id;
        this.name = name;
    }

    private static Component componentUses(int usesLeft, int maxUses) {
        return Component.join(JoinConfiguration.noSeparators(),
            textLightGray("("),
            textLightGreen(String.valueOf(usesLeft)),
            textLightGray("/"),
            textLightGreen(String.valueOf(maxUses)),
            textLightGray(")")
        );
    }

    private static ComponentLike textLightGreen(String text) {
        return Component.text(text, TextColor.color(0x90ee90));
    }

    private static ComponentLike textLightGray(String text) {
        return Component.text(text, TextColor.color(0x999999));
    }

    private static ComponentLike textGray(String text) {
        return Component.text(text, TextColor.color(0x7a7a7a));
    }

    public static ThirstItem fromId(String id) {
        return idToItem.get(id);
    }

    @NotNull
    private static ItemStack resetWaterBottle(@NotNull ItemStack item) {
        if (item.getItemMeta() instanceof PotionMeta potionMeta) {
            potionMeta.setColor(Color.fromRGB(0x395FCA));
            item.setItemMeta(potionMeta);
        }
        return item;
    }

    @NotNull
    private ItemStack commonMaterial(@Nullable ItemStack item, int usesLeft) {
        boolean isEmpty = usesLeft == 0;
        Material material = isEmpty ? Material.GLASS_BOTTLE : Material.POTION;
        if (item == null) {
            return new ItemStack(Material.POTION);
        }
        item.setType(material);
        return item;
    }

    @Nullable
    private Component displayName(int usesLeft, int maxUses, boolean isDirty) {
        if (this == NORMAL_BOTTLE && usesLeft == 0) return null;
        TextComponent name = Component.text(this.name, this.textColor(isDirty));
        Collection<Component> displayNameParts = new ArrayList<>();
        if (isDirty) displayNameParts.add(DIRTY_DISPLAY);
        displayNameParts.add(name);
        displayNameParts.add(componentUses(usesLeft, maxUses));
        return Component.join(JoinConfiguration.separator(Component.space()), displayNameParts);
    }

    @NotNull
    private ItemMeta commonTexture(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(this.texture);
        if (meta instanceof PotionMeta potion)
            potion.setColor(Color.fromRGB(this.color));
        return meta;
    }

    public ItemStack toItemStack(boolean isDirty, int usesLeft) {
        return toItemStack(null, isDirty, usesLeft);
    }

    public ItemStack toItemStack(@Nullable ItemStack item, boolean isDirty, int usesLeft) {
        ExactThirstConfig config = getConfig();

        item = commonMaterial(item, usesLeft);
        ThirstKeys.setAll(item, this.id, usesLeft, isDirty);
        ItemMeta meta = commonTexture(item);

        meta.displayName(this.displayName(usesLeft, config.getUses(), isDirty));

        item.setItemMeta(meta);

        boolean isWaterBottle = this == NORMAL_BOTTLE && usesLeft != 0;
        if (isWaterBottle) return resetWaterBottle(item);
        return item;
    }

    private ExactThirstConfig getConfig() {
        if (this.config != null) return this.config;
        this.config = ThirstConfig.get().getConfig(this.id);
        if (this.config == null)
            PhysicalModule.get().logger().error("There is no '{}' keyedConsumables in the ThirstConfig!", this.id);
        return this.config;
    }

    private TextColor textColor(boolean isDirty) {
        if (isDirty) return TextColor.color(DIRTY_COLOR);
        return TextColor.color(this.color);
    }

    public String getId() {
        return this.id;
    }

    public ItemStack toEmpty() {
        return this.toEmpty(null);
    }

    public ItemStack toEmpty(@Nullable ItemStack item) {
        return this.toItemStack(item, false, 0);
    }

    public ItemStack toFull(boolean isDirty) {
        return this.toFull(null, isDirty);
    }

    public ItemStack toFull(@Nullable ItemStack item, boolean isDirty) {
        if (this == FILTERED_CANTEEN) isDirty = false;
        return this.toItemStack(item, isDirty, getConfig().getUses());
    }
}