package com.voltskiya.mechanics.physical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class PhysicalRecipes {

    private static final List<NamespacedKey> recipes = new ArrayList<>();

    public static List<NamespacedKey> getRecipes() {
        return recipes;
    }

    public static void thirstShapedRecipe(NamespacedKey key, ItemStack result, String[] shape, IngredientMapping... ingredients) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
        shapedRecipe.setCategory(CraftingBookCategory.MISC);
        shapedRecipe.shape(shape);
        Arrays.stream(ingredients).forEach(ing -> shapedRecipe.setIngredient(ing.key, ing.material));
        recipes.add(key);
        Bukkit.addRecipe(shapedRecipe);
    }


    public static void shapelessRecipe(NamespacedKey key, ItemStack result, Material... ingredients) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, result);
        shapelessRecipe.setCategory(CraftingBookCategory.MISC);
        Arrays.stream(ingredients).forEach(shapelessRecipe::addIngredient);
        recipes.add(key);
        Bukkit.addRecipe(shapelessRecipe);
    }

    public static void thirstFurnaceRecipe(NamespacedKey key, ItemStack result, ItemStack input) {
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(key, result, new ExactChoice(input), 0f, 100);
        furnaceRecipe.setCategory(CookingBookCategory.FOOD);
        recipes.add(key);
        Bukkit.addRecipe(furnaceRecipe);
    }

    public static class IngredientMapping {

        private final char key;
        private final Material material;

        public IngredientMapping(char key, Material material) {
            this.key = key;
            this.material = material;
        }
    }
}
