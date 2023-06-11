package com.voltskiya.mechanics.physical.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.VoltskiyaRecipeManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class ThirstRecipes {


    private static NamespacedKey key(String key) {
        return VoltskiyaPlugin.get().namespacedKey("Thirst" + "." + key);
    }

    public static void registerRecipes() {
        VoltskiyaRecipeManager.shaped(key("canteen"), Item.CANTEEN_EMPTY, new String[]{"L  ", "LIL", " L "},
            new VoltskiyaRecipeManager.IngredientMapping('L', Material.LEATHER),
            new VoltskiyaRecipeManager.IngredientMapping('I', Material.IRON_INGOT));
        VoltskiyaRecipeManager.shaped(key("filtered_canteen"), Item.FILTERED_CANTEEN_EMPTY, new String[]{"ISI", "LSL", "LLL"},
            new VoltskiyaRecipeManager.IngredientMapping('I', Material.IRON_INGOT),
            new VoltskiyaRecipeManager.IngredientMapping('I', Material.IRON_INGOT),
            new VoltskiyaRecipeManager.IngredientMapping('S', Material.STRING));
        VoltskiyaRecipeManager.shapeless(key("simple_bottle"), Item.SIMPLE_BOTTLE_EMPTY,
            new VoltskiyaRecipeManager.IngredientChoice(Material.LEATHER));
        VoltskiyaRecipeManager.furnace(key("canteen_purified"), Item.CANTEEN_FULL.toItemStack(), Item.CANTEEN_DIRTY);
        VoltskiyaRecipeManager.furnace(key("bottle_purified"), Item.getWaterBottle(), Item.BOTTLE_DIRTY);
        VoltskiyaRecipeManager.furnace(key("simple_bottle_purified"), Item.SIMPLE_BOTTLE_FULL.toItemStack(), Item.SIMPLE_BOTTLE_DIRTY);
    }
}