package com.voltskiya.mechanics.thirst;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.AbstractVoltPlugin;
import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaRecipeManager;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public class ThirstModule extends AbstractModule {
    private static ThirstModule instance;

    public static ThirstModule get() {
        return instance;
    }

    public ThirstModule() {
        instance = this;
    }

    public void init() {
        ThirstConfig.load();
    }

    public void enable() {
        getPlugin().registerEvents(new ThirstListener());
        new ThirstCommandACF();
        registerRecipes();
    }

    public NamespacedKey getKey(String key) {
        return getPlugin().namespacedKey(getName() + "." + key);
    }

    public static NamespacedKey key(String key) {
        return get().getKey(key);
    }

    public String getName() {
        return "Thirst";
    }

    private void registerRecipes() {
        VoltskiyaRecipeManager.shaped(key("canteen"), Item.CANTEEN_EMPTY, new String[]{"L  ", "LIL", " L "}, new VoltskiyaRecipeManager.IngredientMapping('L', Material.LEATHER), new VoltskiyaRecipeManager.IngredientMapping('I', Material.IRON_INGOT));
        VoltskiyaRecipeManager.shaped(key("filtered_canteen"), Item.FILTERED_CANTEEN_EMPTY, new String[]{"ISI", "LSL", "LLL"}, new VoltskiyaRecipeManager.IngredientMapping('I', Material.IRON_INGOT), new VoltskiyaRecipeManager.IngredientMapping('I', Material.IRON_INGOT), new VoltskiyaRecipeManager.IngredientMapping('S', Material.STRING));
        VoltskiyaRecipeManager.shapeless(key("simple_bottle"), Item.SIMPLE_BOTTLE_EMPTY, new VoltskiyaRecipeManager.IngredientChoice(Material.LEATHER));
        VoltskiyaRecipeManager.furnace(key("canteen_purified"), Item.CANTEEN_FULL.toItemStack(), Item.CANTEEN_DIRTY);
        VoltskiyaRecipeManager.furnace(key("bottle_purified"), ConsumableItemStack.getWaterBottle(), Item.BOTTLE_DIRTY);
        VoltskiyaRecipeManager.furnace(key("simple_bottle_purified"), Item.SIMPLE_BOTTLE_FULL.toItemStack(), Item.SIMPLE_BOTTLE_DIRTY);
    }
}