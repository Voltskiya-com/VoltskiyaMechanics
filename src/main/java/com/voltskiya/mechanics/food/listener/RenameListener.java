package com.voltskiya.mechanics.food.listener;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.food.util.FoodHandler;
import com.voltskiya.mechanics.rotting.Charts;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class RenameListener implements Listener, FoodHandler {

    public RenameListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    /**
     * @return if the player clicked in the result item slot of the anvil inventory
     * @implNote slot 0 = left item slot
     * @implNote slot 1 = right item slot
     * @implNote slot 2 = result item slot
     * <p>
     */
    private static boolean isResultSlot(int rawSlot) {
        return rawSlot == 2;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        // see if the event is about an anvil
        if (!(event.getInventory() instanceof AnvilInventory)) return;

        InventoryView view = event.getView();
        int rawSlot = event.getRawSlot();
        // compare the raw slot with the inventory view to make sure we are talking about the upper inventory
        if (rawSlot != view.convertSlot(rawSlot)) return;

        if (!isResultSlot(rawSlot)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir())
            return;
        if (!foodConfig().hasFoodTimer(item.getType())) return;
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(Charts.vanilla, PersistentDataType.INTEGER, 1);
        item.setItemMeta(im);
        event.setCurrentItem(item);
        //todo verify it works or if it's even needed
    }
}