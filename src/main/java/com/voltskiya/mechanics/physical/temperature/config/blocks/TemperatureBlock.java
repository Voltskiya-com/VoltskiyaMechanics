package com.voltskiya.mechanics.physical.temperature.config.blocks;

import apple.mc.utilities.inventory.item.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TemperatureBlock {

    protected double temperature;
    protected Material material;

    public TemperatureBlock() {
    }

    public TemperatureBlock(TempBlockTypeBuilder builder) {
        this.material = builder.material;
        this.temperature = builder.temperature;
    }

    public String getName() {
        return material.name();
    }

    public TempBlockTypeBuilder toBuilder() {
        return new TempBlockTypeBuilder(this);
    }

    public double getTemperature() {
        return this.temperature;
    }

    public ItemStack toItem() {
        return InventoryUtils.get()
            .makeItem(material.isItem() ? material : Material.BLACK_CONCRETE, 1,
                material.name(), null);
    }

    public static class TempBlockTypeBuilder {

        private Material material = Material.AIR;
        private double temperature = 0;

        private TempBlockTypeBuilder(TemperatureBlock real) {
            this.material = real.material;
            this.temperature = real.temperature;
        }

        public TempBlockTypeBuilder() {
        }

        public TemperatureBlock build() {
            return new TemperatureBlock(this);
        }

        public void incrementTemp(int increment) {
            this.temperature += increment;
        }

        public void setType(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }

        public double getTemperature() {
            return temperature;
        }
    }
}
