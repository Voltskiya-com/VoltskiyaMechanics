package com.voltskiya.mechanics.thirst.config;

import com.voltskiya.mechanics.thirst.ThirstItem;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ThirstConsumableVariant {

    private final Material material = Material.POTION;
    private final int modelData = 0;
    private final Component title = Component.text("Unknown").color(NamedTextColor.DARK_PURPLE);
    private final List<Component> lore = Collections.emptyList();

    private transient ItemStack item = null;

    public ItemStack getCopy() {
        return this.item.clone();
    }

    public void setDirty() {
        ThirstItem.setIsDirty(this.item, true);
    }

    public void setFull(int uses) {
        ThirstItem.setUses(this.item, uses);
    }

    public void setKey(String key) {
        item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer()
            .set(ThirstConsumableConfig.THIRST_CONSUMALBE_KEY, PersistentDataType.STRING, key);
        itemMeta.setCustomModelData(modelData);
        itemMeta.displayName(title);
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
    }
}
