package com.voltskiya.mechanics.thirst;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.BukkitCommandManager;
import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;

public class ThirstModule extends AbstractModule {
    private static BukkitCommandManager acf;

    public static void registerCommand(BaseCommand command) {
        acf.registerCommand(command);
    }

    public static ThirstModule instance;

    public static ThirstModule get() {
        return instance;
    }

    public ThirstModule(){
        instance = this;
    }
    @Override
    public void init() {
        ThirstConfig.load();
    }

    @Override
    public void enable() {
        ThirstyPlayer.load();
        Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), ThirstyPlayer::updatePlayers, 0, 20);
        getPlugin().registerEvents(new ThirstListener());
        acf = new BukkitCommandManager(getPlugin());
        new ThirstCommandACF();
        registerRecipes();
    }
    @Override
    public String getName() {
        return "Thirst";
    }


    @Override
    public void onDisable() {
        ThirstyPlayer.save();
    }


    private void registerRecipes() {
        ShapedRecipe canteen = new ShapedRecipe(
            new NamespacedKey(ThirstModule.get().getPlugin(), "canteen"), Item.CANTEEN_EMPTY.toItemStack());
        canteen.shape(
            "L  ",
            "LIL",
            " L ");
        canteen.setIngredient('L', Material.LEATHER);
        canteen.setIngredient('I', Material.IRON_INGOT);
        Bukkit.addRecipe(canteen);

        ShapelessRecipe simpleBottle = new ShapelessRecipe(
            new NamespacedKey(ThirstModule.get().getPlugin(), "simple_bottle"),
            Item.SIMPLE_BOTTLE_EMPTY.toItemStack());
        simpleBottle.addIngredient(Material.LEATHER);
        Bukkit.addRecipe(simpleBottle);

        ShapedRecipe filteredCanteen = new ShapedRecipe(
            new NamespacedKey(ThirstModule.get().getPlugin(), "filtered_canteen"),
            Item.FILTERED_CANTEEN_EMPTY.toItemStack());
        filteredCanteen.shape(
            "ISI",
            "LSL",
            "LLL");
        filteredCanteen.setIngredient('I', Material.IRON_INGOT);
        filteredCanteen.setIngredient('L', Material.LEATHER);
        filteredCanteen.setIngredient('S', Material.STRING);
        Bukkit.addRecipe(filteredCanteen);

        RecipeManager recipeManager = ((CraftServer) Bukkit.getServer()).getServer().getRecipeManager();
        recipeManager.addRecipe(new SmeltingRecipe(CraftNamespacedKey.toMinecraft(VoltskiyaPlugin.get().namespacedKey("canteen_purified")),
                "",
                new Ingredient(List.of(new Ingredient.ItemValue(CraftItemStack.asNMSCopy(Item.CANTEEN_DIRTY.toItemStack()))).stream()),
                CraftItemStack.asNMSCopy(Item.CANTEEN_FULL.toItemStack()),
                0,
                200));
        ItemStack waterBottle = new ItemStack(Material.POTION);
        PotionMeta pmeta = (PotionMeta) waterBottle.getItemMeta();
        PotionData pdata = new PotionData(PotionType.WATER);
        pmeta.setBasePotionData(pdata);
        waterBottle.setItemMeta(pmeta);
        recipeManager.addRecipe(new SmeltingRecipe(CraftNamespacedKey.toMinecraft(VoltskiyaPlugin.get().namespacedKey("bottle_purified")),
                "",
                new Ingredient(List.of(new Ingredient.ItemValue(CraftItemStack.asNMSCopy(Item.BOTTLE_DIRTY.toItemStack()))).stream()),
                CraftItemStack.asNMSCopy(waterBottle),
                0,
                200));
        recipeManager.addRecipe(new SmeltingRecipe(CraftNamespacedKey.toMinecraft(VoltskiyaPlugin.get().namespacedKey("simple_bottle_purified")),
                "",
                new Ingredient(List.of(new Ingredient.ItemValue(CraftItemStack.asNMSCopy(Item.SIMPLE_BOTTLE_DIRTY.toItemStack()))).stream()),
                CraftItemStack.asNMSCopy(Item.SIMPLE_BOTTLE_FULL.toItemStack()),
                0,
                200));
    }
}
