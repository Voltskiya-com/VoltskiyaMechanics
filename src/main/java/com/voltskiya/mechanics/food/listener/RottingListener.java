package com.voltskiya.mechanics.food.listener;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.food.config.FoodRotConfig;
import com.voltskiya.mechanics.food.listener.hopper.RottingHopper;
import com.voltskiya.mechanics.food.service.RottingDecrement;
import com.voltskiya.mechanics.food.util.merge.RottingMerge;
import com.voltskiya.mechanics.food.util.merge.RottingShiftMoveResult;
import com.voltskiya.mechanics.rotting.ContainerRottingDecrement;
import org.bukkit.Location;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Item;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RottingListener implements Listener {


    public RottingListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    private static void pickup(Cancellable event, Item item, Inventory destination) {
        ItemStack source = item.getItemStack();
        RottingShiftMoveResult result = RottingMerge.shiftMove(destination, source);
        if (result.isComplete()) {
            item.remove();
            event.setCancelled(true);
        } else if (result.isPartial()) {
            event.setCancelled(true);
        }
    }

    private static boolean isPlayerInventory(InventoryType type) {
        return type == InventoryType.WORKBENCH || type == InventoryType.PLAYER;
    }

    private static boolean hasFoodTimer(ItemStack currentItem) {
        return FoodRotConfig.get().hasFoodTimer(currentItem.getType());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        RottingDecrement.addInventory(event.getInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMove(InventoryMoveItemEvent event) {
        InventoryHolder initiator = event.getInitiator().getHolder();
        if (initiator instanceof Hopper hopper) {
            Location location = hopper.getBlock().getLocation();
            RottingHopper.addEvent(location, event);
        } else if (initiator instanceof HopperMinecart hopperMinecart) {
            RottingHopper.addEvent(hopperMinecart.getUniqueId(), event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if (currentItem == null || cursor == null) return;
        if (!hasFoodTimer(currentItem) || !hasFoodTimer(cursor)) return;

        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        if (!isPlayerInventory(inventory.getType())) {
            Inventory topInventory = event.getInventory();
            new ContainerRottingDecrement(topInventory).tick();
        }

        ItemStack source;
        ItemStack destination;
        Integer countToMerge;
        switch (event.getAction()) {
            case PICKUP_ONE -> {
                source = currentItem;
                destination = cursor;
                countToMerge = 1;
            }
            case NOTHING -> {
            }
            case PICKUP_ALL, COLLECT_TO_CURSOR, PICKUP_SOME -> {
                source = currentItem;
                destination = cursor;
            }
            case PLACE_ONE -> {
                source = cursor;
                destination = currentItem;
                countToMerge = 1;
            }
            case PLACE_ALL, SWAP_WITH_CURSOR, PLACE_SOME -> {
                source = cursor;
                destination = currentItem;
            }
            case PICKUP_HALF, DROP_ALL_CURSOR, DROP_ONE_SLOT, DROP_ALL_SLOT, DROP_ONE_CURSOR -> {
                return;
            }
            case MOVE_TO_OTHER_INVENTORY -> {
                if (inventory instanceof PlayerInventory pInventory) {
                    RottingMerge.playerShiftMove(pInventory, event.getView(), event.getSlotType(), currentItem);
                }
            }
            case HOTBAR_MOVE_AND_READD -> {
            }
            case HOTBAR_SWAP -> {
            }
            case CLONE_STACK -> {
            }
            case UNKNOWN -> {
            }
        }
        // todo
//        if (source == null || destination == null) return;
//        FoodItem sourceFood = new FoodItem(destination, null);
//        new FoodItem(source, null).mergeIntoOther(sourceFood);
//        event.setCancelled(true);

    }

    @EventHandler(ignoreCancelled = true)
    public void onHopperPickup(InventoryPickupItemEvent event) {
        pickup(event, event.getItem(), event.getInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof InventoryHolder player) {
            pickup(event, event.getItem(), player.getInventory());
        }
    }
}
