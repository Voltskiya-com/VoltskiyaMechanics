package com.voltskiya.mechanics.physical.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.PhysicalRecipes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class ThirstRecipes {


    private static NamespacedKey key(String key) {
        return VoltskiyaPlugin.get().namespacedKey("Thirst" + "." + key);
    }

    public static void registerRecipes() {
        PhysicalRecipes.shaped(key("canteen"), Item.CANTEEN_EMPTY, new String[]{"L  ", "LIL", " L "},
            new PhysicalRecipes.IngredientMapping('L', Material.LEATHER),
            new PhysicalRecipes.IngredientMapping('I', Material.IRON_INGOT));
        PhysicalRecipes.shaped(key("filtered_canteen"), Item.FILTERED_CANTEEN_EMPTY, new String[]{"ISI", "LSL", "LLL"},
            new PhysicalRecipes.IngredientMapping('I', Material.IRON_INGOT),
            new PhysicalRecipes.IngredientMapping('I', Material.IRON_INGOT),
            new PhysicalRecipes.IngredientMapping('S', Material.STRING));
        PhysicalRecipes.shapeless(key("simple_bottle"), Item.SIMPLE_BOTTLE_EMPTY,
            new PhysicalRecipes.IngredientChoice(Material.LEATHER));
        PhysicalRecipes.furnace(key("canteen_purified"), Item.CANTEEN_FULL.toItemStack(), Item.CANTEEN_DIRTY);
        PhysicalRecipes.furnace(key("bottle_purified"), Item.getWaterBottle(), Item.BOTTLE_DIRTY);
        PhysicalRecipes.furnace(key("simple_bottle_purified"), Item.SIMPLE_BOTTLE_FULL.toItemStack(), Item.SIMPLE_BOTTLE_DIRTY);
    }
}