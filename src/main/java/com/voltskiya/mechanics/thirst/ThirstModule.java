package com.voltskiya.mechanics.thirst;

import com.voltskiya.lib.AbstractModule;
import com.voltskiya.lib.acf.BaseCommand;
import com.voltskiya.lib.acf.BukkitCommandManager;
import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.thirst.config.ThirstConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class ThirstModule extends AbstractModule {
    public static final NamespacedKey THIRST_CONSUMABLE_KEY = VoltskiyaPlugin.get().namespacedKey("thirst.consumable");
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
    }
}
