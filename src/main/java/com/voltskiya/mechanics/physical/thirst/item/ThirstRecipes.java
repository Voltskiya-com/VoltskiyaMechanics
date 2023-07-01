package com.voltskiya.mechanics.physical.thirst.item;

import static com.voltskiya.mechanics.physical.PhysicalRecipes.shapelessRecipe;
import static com.voltskiya.mechanics.physical.PhysicalRecipes.thirstFurnaceRecipe;
import static com.voltskiya.mechanics.physical.PhysicalRecipes.thirstShapedRecipe;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.PhysicalRecipes.IngredientMapping;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ThirstRecipes {


    private static NamespacedKey key(String key) {
        return VoltskiyaPlugin.get().namespacedKey("thirst" + "." + key);
    }

    public static void registerRecipes() {
        ItemStack canteenEmpty = ThirstItem.CANTEEN.toEmpty();
        ItemStack canteenDirty = ThirstItem.CANTEEN.toFull(true);
        ItemStack canteenPurified = ThirstItem.CANTEEN.toFull(false);

        ItemStack filteredCanteenEmpty = ThirstItem.FILTERED_CANTEEN.toEmpty();

        ItemStack simpleBottleEmpty = ThirstItem.SIMPLE_BOTTLE.toEmpty();
        ItemStack simpleBottleDirty = ThirstItem.SIMPLE_BOTTLE.toFull(true);
        ItemStack simpleBottlePurified = ThirstItem.SIMPLE_BOTTLE.toFull(false);

        registerCrafting(canteenEmpty, filteredCanteenEmpty, simpleBottleEmpty);

        ItemStack waterDirty = ThirstItem.NORMAL_BOTTLE.toFull(true);
        ItemStack waterPurified = ThirstItem.NORMAL_BOTTLE.toFull(false);

        thirstFurnaceRecipe(key("canteen_purified"), canteenPurified, canteenDirty);
        thirstFurnaceRecipe(key("simple_bottle_purified"), simpleBottlePurified, simpleBottleDirty);

        thirstFurnaceRecipe(key("bottle_purified"), waterPurified, waterDirty);
    }

    public static ItemStack waterBottleItem() {
        ItemStack bottle = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) bottle.getItemMeta();
        PotionData data = new PotionData(PotionType.WATER);
        meta.setBasePotionData(data);
        bottle.setItemMeta(meta);
        return bottle;
    }

    public static void registerCrafting(ItemStack canteenEmpty, ItemStack filteredCanteenEmpty, ItemStack simpleBottleEmpty) {
        thirstShapedRecipe(key("canteen"), canteenEmpty, new String[]{"L  ", "LIL", " L "},
            new IngredientMapping('L', Material.LEATHER),
            new IngredientMapping('I', Material.IRON_INGOT));

        thirstShapedRecipe(key("filtered_canteen"), filteredCanteenEmpty, new String[]{"ISI", "LSL", "LLL"},
            new IngredientMapping('L', Material.LEATHER),
            new IngredientMapping('I', Material.IRON_INGOT),
            new IngredientMapping('S', Material.STRING));

        shapelessRecipe(key("simple_bottle"), simpleBottleEmpty, Material.LEATHER);
    }
}