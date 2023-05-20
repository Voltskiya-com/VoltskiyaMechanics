package com.voltskiya.mechanics.food.util;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.food.config.CoolerConfig;
import com.voltskiya.mechanics.food.config.CoolerConfigDatabase;
import com.voltskiya.mechanics.food.config.CoolerItemConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public interface CoolerHandler {

    NamespacedKey COOLER_KEY = VoltskiyaPlugin.get().namespacedKey("rotting.cooler");

    default CoolerItemConfig getCoolerConfig(TileState inventory) {
        return getCoolerConfig(inventory.getPersistentDataContainer());
    }

    default CoolerConfig getCoolerConfigOrFallback(Inventory inventory) {
        if (inventory instanceof TileState cooler)
            return getCoolerConfig(cooler);
        else
            return CoolerConfigDatabase.get().getDefaultConfig();
    }


    default CoolerItemConfig getCoolerConfig(ItemStack item) {
        return getCoolerConfig(item.getItemMeta().getPersistentDataContainer());
    }

    private CoolerItemConfig getCoolerConfig(PersistentDataContainer container) {
        Integer id = container.get(COOLER_KEY, PersistentDataType.INTEGER);
        if (id == null) return null;
        return CoolerConfigDatabase.get().getConfig(id);
    }

    default void setBlockStateToCooler(TileState state, CoolerItemConfig config) {
        PersistentDataContainer container = state.getPersistentDataContainer();
        container.set(COOLER_KEY, PersistentDataType.INTEGER, config.getId());
        if (state instanceof Nameable chest)
            chest.customName(Component.text(config.getDisplayName()));
        state.update();
    }
}
