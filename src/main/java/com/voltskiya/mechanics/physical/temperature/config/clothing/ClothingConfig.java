package com.voltskiya.mechanics.physical.temperature.config.clothing;

import com.voltskiya.lib.configs.data.config.init.AppleConfigInit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ClothingConfig extends AppleConfigInit {

    private static final ClothingItemType FALLBACK = new ClothingItemType();
    private static ClothingConfig instance;
    protected Map<EquipmentSlot, Double> fallbackEquipmentRatios = new HashMap<>(Map.of(
        EquipmentSlot.HEAD,
        0.375,

        EquipmentSlot.CHEST,
        1d,
        EquipmentSlot.LEGS,
        0.75,
        EquipmentSlot.FEET,
        0.375
    ));
    protected double fallbackEquipmentRatiosMultiplier = 1;
    protected List<ClothingSet> clothingSets = new ArrayList<>();
    protected transient Map<Material, ClothingItemType> clothing = new HashMap<>();

    public static ClothingConfig get() {
        return instance;
    }

    @Override
    public void onInitConfig() {
        instance = this;
        if (fallbackEquipmentRatiosMultiplier != 1) {
            if (fallbackEquipmentRatiosMultiplier == 0) throw new IllegalStateException("don't reset the fallbackEquipmentRatios!");
            for (EquipmentSlot slot : fallbackEquipmentRatios.keySet()) {
                fallbackEquipmentRatios.computeIfPresent(slot, (slot1, val) -> val * fallbackEquipmentRatiosMultiplier);
            }
            this.fallbackEquipmentRatiosMultiplier = 1;
            this.save();
        }
        for (ClothingSet set : clothingSets) {
            for (ClothingItemType item : set.listItems()) {
                this.clothing.put(item.getMaterial(), item);
            }
        }
    }

    @NotNull
    public HasClothingProtection getClothing(ItemStack item) {
        if (item == null) return FALLBACK;
        ClothingItemType clothingType = clothing.getOrDefault(item.getType(), FALLBACK);
        return new ClothingItem(item, clothingType);
    }
}
