package com.voltskiya.mechanics.physical;

import com.voltskiya.mechanics.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

@UtilityClass
public class PhysicalRecipes {

    @Getter
    private final List<NamespacedKey> recipes = new ArrayList<>();


    @Getter(lazy = true)
    private final RecipeManager recipeManager = ((CraftServer) Bukkit.getServer()).getServer().getRecipeManager();

    public void shaped(NamespacedKey key, Item result, String[] shape, PhysicalRecipes.IngredientMapping... ingredients) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, result.toItemStack());
        shapedRecipe.shape(shape);
        Arrays.stream(ingredients).forEach(im -> shapedRecipe.setIngredient(im.getKey(), im.getRecipeChoice()));
        recipes.add(key);
        Bukkit.addRecipe(shapedRecipe);
    }

    public void shapeless(NamespacedKey key, Item result, PhysicalRecipes.IngredientChoice... ingredients) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, result.toItemStack());
        Arrays.stream(ingredients).forEach(ic -> shapelessRecipe.addIngredient(ic.getRecipeChoice()));
        recipes.add(key);
        Bukkit.addRecipe(shapelessRecipe);
    }

    public void furnace(NamespacedKey key, ItemStack result, Item input) {
        recipes.add(key);
        Ingredient ingredient = new Ingredient(
            List.of(new Ingredient.ItemValue(CraftItemStack.asNMSCopy(input.toItemStack()))).stream());
        ingredient.exact = true;
        SmeltingRecipe smeltingRecipe = new SmeltingRecipe(CraftNamespacedKey.toMinecraft(key), "", CookingBookCategory.FOOD,
            ingredient, CraftItemStack.asNMSCopy(result), 0.0F, 100);
        getRecipeManager().addRecipe(smeltingRecipe);
    }

    @AllArgsConstructor
    @Getter
    public static class IngredientChoice {

        private final RecipeChoice recipeChoice;

        public IngredientChoice(Material material) {
            this(new RecipeChoice.MaterialChoice(List.of(material)));
        }

        public IngredientChoice(ItemStack itemStack) {
            this(new RecipeChoice.ExactChoice(itemStack));
        }

        public IngredientChoice(Item item) {
            this(item.toItemStack());
        }
    }

    @AllArgsConstructor
    @Getter
    public static class IngredientMapping {

        private final char key;
        private final RecipeChoice recipeChoice;

        public IngredientMapping(char key, Material material) {
            this(key, new RecipeChoice.MaterialChoice(List.of(material)));
        }

        public IngredientMapping(char key, ItemStack itemStack) {
            this(key, new RecipeChoice.ExactChoice(itemStack));
        }

        public IngredientMapping(char key, Item item) {
            this(key, item.toItemStack());
        }
    }
}
