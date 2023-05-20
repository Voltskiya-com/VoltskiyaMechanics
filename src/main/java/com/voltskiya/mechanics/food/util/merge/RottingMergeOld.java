package com.voltskiya.mechanics.food.util.merge;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.food.util.FoodHandler;
import com.voltskiya.mechanics.rotting.Charts;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class RottingMergeOld implements Listener, FoodHandler {

    public RottingMergeOld() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    public static boolean pushItem(Inventory destination, ItemStack oldItem) {
        ItemStack[] destinationContents = destination.getContents();
        // get the first item where it would accept me
        for (int i = 0; i < destinationContents.length; i++) {
            if (pushItem(destination, oldItem, destinationContents[i], i)) return true;
            // otherwise try the other items
        }

        return false;
    }

    public static boolean pushItem(Inventory destination, ItemStack oldItem, ItemStack destinationContent, int slot) {
        if (destinationContent == null || destinationContent.getType().isAir()) {
            // this shouldn't ever happen as the item should have been moved without me doing anything
            ItemStack newItem = new ItemStack(oldItem);
            newItem.setAmount(1);
            destination.setItem(slot, newItem);
            oldItem.setAmount(oldItem.getAmount() - 1);
            return true;
        }
        return true;
//        return mergeItem(oldItem, destinationContent);
    }


    public static void shiftMovePlayerInv(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null)
            // i don't care if you click outside the inventory
            return;

        ItemStack source = event.getCurrentItem();
//        if (source == null || source.getType() == Material.AIR || !IsRottable.isRottable(source.getType()))
//            // i don't care if they're not shiftclicking anything
//            return;
        /*
        0 is for hotbar
        1 is for inventory
        2 is for crafting / armor / shield
         */
        int sourceType;
        if (clickedInventory.getType() == InventoryType.CRAFTING) {
            sourceType = 2;
        } else if (clickedInventory.getType() == InventoryType.PLAYER) {
            int slot = event.getSlot();
            if (slot <= 8 && slot >= 0) {
                sourceType = 0;
            } else if (slot >= 9 && slot <= 35) {
                sourceType = 1;
            } else if (slot >= 36 && slot <= 39) {
                sourceType = 1;
            } else if (slot == 40) {
                sourceType = 1;
            } else
                // i don't care about this weird inventory slot
                return;
        } else
            // this is some weird inventory?
            return;
        // we have a source

        PlayerInventory inventory = event.getWhoClicked().getInventory();
        @NotNull ItemStack[] contents = inventory.getContents();

        int lower;
        int upper;
        switch (sourceType) {
            case 0:
                lower = 10;
                upper = 36;
                break;
            case 1:
                lower = 0;
                upper = 9;
                break;
            case 2:
                lower = 0;
                upper = 36;
                break;
            default:
                return;
        }
        for (int i = lower; i <= upper; i++) {
            ItemStack content = contents[i];
            if (content == null || content.getType() == Material.AIR)
                continue;
            // if these two items are the same type
            if (content.getType().equals(source.getType())) {
                // try to merge them
                int leftToStackContent = content.getMaxStackSize() - content.getAmount();
                if (leftToStackContent == 0) {
                    continue;
                }
//                mergeItem(true, source, content);
                if (source.getAmount() <= 0) {
                    source.setAmount(0);
                    break;
                }
            }
        }
        if (source.getAmount() != 0) {
            // if there is anything left, put it in an empty slot
            for (int i = lower; i < upper; i++) {
                ItemStack item = contents[i];
                if (item == null || item.getType() == Material.AIR) {
                    inventory.setItem(i, new ItemStack(source));
                    source.setAmount(0);
                    break;
                }
            }
        }
        event.setCancelled(true);
    }

    public boolean mergeItem(ItemStack source, ItemStack destination, int mergeCount) {

        ItemMeta cursorMeta = source.getItemMeta();
        ItemMeta invMeta = destination.getItemMeta();

        // check if one is vanilla. if it is, ignore this event

//        if (!invIsVanilla) {
//            Integer vanilla = invMeta.getPersistentDataContainer().get(Charts.vanilla, PersistentDataType.INTEGER);
//            invIsVanilla = vanilla != null && vanilla == 1;
//            if (!invIsVanilla) return false;
//        }
        @NotNull PersistentDataContainer cursorContainer = cursorMeta.getPersistentDataContainer();
        @NotNull PersistentDataContainer invContainer = invMeta.getPersistentDataContainer();

        Long cursorCountdown = cursorContainer.get(Charts.rottingCountdownKey, PersistentDataType.LONG);
        Long invCountdown = invContainer.get(Charts.rottingCountdownKey, PersistentDataType.LONG);

        if (cursorCountdown == null) {
//            Long timeToRot = Charts.rottingChart.getOrDefault(source.getType().toString(), (long) -1);
//            if (timeToRot == -1)
//                // ignore this event
//                return false;
//            RottingDecrement.giveFirstRot(source, cursorMeta, cursorContainer, timeToRot * 1000);
//            cursorCountdown = cursorContainer.get(Charts.rottingCountdownKey, PersistentDataType.LONG);
//            if (cursorCountdown == null)
//                // ignore
//                return false;

        }

        if (invCountdown == null) {
//            Long timeToRot = Charts.rottingChart.getOrDefault(destination.getType().toString(), (long) -1);
//            if (timeToRot == -1)
//                // ignore this event
//                return false;
//            RottingDecrement.giveFirstRot(destination, invMeta, invContainer, timeToRot * 1000);
//            invCountdown = invContainer.get(Charts.rottingCountdownKey, PersistentDataType.LONG);
//            if (invCountdown == null)
//                // ignore
//                return false;
        }
        int cursorCount = source.getAmount();
//        if (!isAll)
//            cursorCount = 1;
        int invCount = destination.getAmount();

        int maxStackSize = destination.getMaxStackSize();

        // num of items being moved
        int numToMove = Math.min(maxStackSize - invCount, cursorCount);

        long newCountdown = Math.min(invCountdown, cursorCountdown);

        // set the item in the inventory
        destination.setAmount(invCount + numToMove);
        List<String> lore = new ArrayList<>();
        if (newCountdown == -1) {
            // shouldn't ever happen
            newCountdown = -2;
        }
//        invMeta.setLore(RottingDecrement.getLore(newCountdown));
        invContainer.set(Charts.lastCheckedKey, PersistentDataType.LONG, System.currentTimeMillis());
        invContainer.set(Charts.rottingCountdownKey, PersistentDataType.LONG, newCountdown);
        destination.setItemMeta(invMeta);

        // set the amount of the item in the cursor
        source.setAmount(source.getAmount() - numToMove);
        return numToMove != 0;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        ClickType type = event.getClick();
        ItemStack cursorItem = event.getCursor();
        ItemStack invItem = event.getCurrentItem();

        // if it's a crafting recipe, dont mess with it
        Inventory destination = event.getInventory();
        if (destination.getType() == InventoryType.CRAFTING || destination.getType() == InventoryType.WORKBENCH) {
            if (event.getSlot() == 0) {
                return;
            }
        }
        // make sure it's not a non vanilla
        boolean cursorItemExists = !(cursorItem == null || cursorItem.getType() == Material.AIR);
        boolean invItemExists = !(invItem == null || invItem.getType() == Material.AIR);

        if (cursorItemExists && invItemExists && cursorItem.getType() == invItem.getType()) {
            ItemMeta cursorMeta = cursorItem.getItemMeta();
            ItemMeta invMeta = invItem.getItemMeta();
            if (cursorMeta == null || invMeta == null) {
                return;
            }
            boolean cursorIsVanilla = cursorMeta.getDisplayName().isEmpty();
            boolean invIsVanilla = invMeta.getDisplayName().isEmpty();
            if (!cursorIsVanilla) {
                Integer vanilla = cursorMeta.getPersistentDataContainer().get(Charts.vanilla, PersistentDataType.INTEGER);
                cursorIsVanilla = vanilla != null && vanilla == 1;
                if (!cursorIsVanilla) {
                    return;
                }
            }
            if (!invIsVanilla) {
                Integer vanillaInv = invMeta.getPersistentDataContainer().get(Charts.vanilla, PersistentDataType.INTEGER);
                invIsVanilla = vanillaInv != null && vanillaInv == 1;
                if (!invIsVanilla) {
                    return;
                }
            }
        }

        if (type.equals(ClickType.LEFT) || type.equals(ClickType.RIGHT)) {
//            if (cursorItemExists && invItemExists && cursorItem.getType() == invItem.getType() && IsRottable.isRottable(
//                invItem.getType())) {
//                boolean isAll = type == ClickType.LEFT;
//                if (mergeItem(isAll, cursorItem, invItem))
//                    event.setCancelled(true);
//            }
        } else if (type.equals(ClickType.SHIFT_LEFT) || type.equals(ClickType.SHIFT_RIGHT)) {

            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                return;
            }
            if (destination.getType() == InventoryType.CRAFTING) {
                shiftMovePlayerInv(event);
                return;
            }
            if (destination.equals(event.getClickedInventory())) {
                destination = event.getWhoClicked().getInventory();
            }
//            if (currentItem.getAmount() != shiftMove(destination, currentItem)) {
//                event.setCancelled(true);
//            }
        }
    }
}