package com.voltskiya.mechanics.rotting;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryType;

public class Charts {

    public static NamespacedKey lastCheckedKey;
    public static NamespacedKey rottingCountdownKey;
    public static NamespacedKey vanilla;
    public static ImmutableSet<InventoryType> furanceTypes = ImmutableSet.of(
        InventoryType.FURNACE,
        InventoryType.BLAST_FURNACE,
        InventoryType.SMOKER
    );
}
