package com.voltskiya.mechanics.physical.temperature.config.clothing;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import java.util.Objects;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ClothingItem implements HasClothingProtection {

    private static final String PROTECTION = "protection";
    private static final String RESISTANCE = "resistance";
    private static final NamespacedKey WIND_PROTECTION_KEY = key(PROTECTION, "wind");
    private static final NamespacedKey WET_PROTECTION_KEY = key(PROTECTION, "wet");
    private static final NamespacedKey WET_RESISTANCE_KEY = key(RESISTANCE, "wet");
    private static final NamespacedKey HEAT_PROTECTION_KEY = key(PROTECTION, "heat");
    private static final NamespacedKey HEAT_RESISTANCE_KEY = key(RESISTANCE, "heat");
    private static final NamespacedKey COLD_PROTECTION_KEY = key(PROTECTION, "cold");
    private static final NamespacedKey COLD_RESISTANCE_KEY = key(RESISTANCE, "cold");
    private final PersistentDataContainer container;
    private final ClothingItemType type;
    private final double windProtection;
    private final double wetProtection;
    private final double wetResistance;
    private final double heatProtection;
    private final double heatResistance;
    private final double coldProtection;
    private final double coldResistance;

    public ClothingItem(ItemStack item, @NotNull ClothingItemType type) {
        this.container = item.getItemMeta().getPersistentDataContainer();
        this.type = type;
        this.windProtection = getOverride(WIND_PROTECTION_KEY, type::getWindProtection);
        this.wetProtection = getOverride(WET_PROTECTION_KEY, type::getWetProtection);
        this.wetResistance = getOverride(WET_RESISTANCE_KEY, type::getWetResistance);
        this.heatProtection = getOverride(HEAT_PROTECTION_KEY, type::getHeatProtection);
        this.heatResistance = getOverride(HEAT_RESISTANCE_KEY, type::getHeatResistance);
        this.coldProtection = getOverride(COLD_PROTECTION_KEY, type::getColdProtection);
        this.coldResistance = getOverride(COLD_RESISTANCE_KEY, type::getColdResistance);
    }

    private static NamespacedKey key(String key, String subKey) {
        return VoltskiyaPlugin.get().namespacedKey("temperature.%s.%s".formatted(key, subKey));
    }


    private double getOverride(NamespacedKey key, Supplier<Double> orElse) {
        Double override = container.get(key, PersistentDataType.DOUBLE);
        return Objects.requireNonNullElseGet(override, orElse);
    }

    @Override
    public Material getMaterial() {
        return this.type.getMaterial();
    }

    @Override
    public double getWindProtection() {
        return windProtection;
    }

    @Override
    public double getWetProtection() {
        return wetProtection;
    }

    @Override
    public double getWetResistance() {
        return wetResistance;
    }

    @Override
    public double getHeatProtection() {
        return heatProtection;
    }

    @Override
    public double getHeatResistance() {
        return heatResistance;
    }

    @Override
    public double getColdProtection() {
        return coldProtection;
    }

    @Override
    public double getColdResistance() {
        return coldResistance;
    }
}
