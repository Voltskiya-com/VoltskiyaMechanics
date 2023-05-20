package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class ThirstListener implements Listener {

    public static final double MAX_REACH_DISTANCE = 5.0D;

    @EventHandler
    public void onCauldron(CauldronLevelChangeEvent e) {
        // TODO fill bottle with appropriate amount of clean water
    }

    @EventHandler
    public void onClickWater(PlayerInteractEvent e) {
        if (EquipmentSlot.HAND != e.getHand())
            return;
        Action action = e.getAction();
        ItemStack itemStack = e.getItem();
        if (null == itemStack)
            return;
        Player player = e.getPlayer();
        if (Action.LEFT_CLICK_AIR == action) {
            emptyContainer(itemStack, player);
            return;
        }
        if (!action.isRightClick())
            return;
        Location location = player.getEyeLocation();
        RayTraceResult raytrace = location.getWorld()
            .rayTraceBlocks(location, location.getDirection(), MAX_REACH_DISTANCE, FluidCollisionMode.SOURCE_ONLY, true);
        if (null == raytrace)
            return;
        Block block = raytrace.getHitBlock();
        if (null == block)
            return;
        if (Material.WATER == block.getType())
            e.setCancelled(fillContainer(itemStack, player));
    }

    private boolean fillContainer(ItemStack bukkitItem, Player player) {
        VoltskiyaItemStack voltskiyaItemStack = new VoltskiyaItemStack(bukkitItem);
        Item item = voltskiyaItemStack.getItem();
        if (Item.UNKNOWN == item && Material.GLASS_BOTTLE == bukkitItem.getType()) {
            voltskiyaItemStack.change1To(Item.BOTTLE_DIRTY, player);
            return true;
        }
        switch (item) {
            case CANTEEN_EMPTY:
                voltskiyaItemStack.change1To(Item.CANTEEN_DIRTY, player);
                return true;
            case SIMPLE_BOTTLE_EMPTY:
                voltskiyaItemStack.change1To(Item.SIMPLE_BOTTLE_DIRTY, player);
                return true;
            case FILTERED_CANTEEN_EMPTY:
                voltskiyaItemStack.change1To(Item.FILTERED_CANTEEN_FULL, player);
                return true;
            default:
                return false;
        }
    }

    private void emptyContainer(ItemStack bukkitItem, Player player) {
        VoltskiyaItemStack itemStack = new VoltskiyaItemStack(bukkitItem);
        if (Material.POTION != itemStack.getItemStack().getType())
            return;
        Item item = itemStack.getItem();
        if (Item.BOTTLE_DIRTY == item)
            itemStack.getItemStack().setType(Material.GLASS_BOTTLE);
        else if (Item.SIMPLE_BOTTLE_DIRTY == item || Item.SIMPLE_BOTTLE_FULL == item)
            itemStack.change1To(Item.SIMPLE_BOTTLE_EMPTY, player);
        else if (Item.CANTEEN_DIRTY == item || Item.CANTEEN_FULL == item)
            itemStack.change1To(Item.CANTEEN_EMPTY, player);
        else if (Item.FILTERED_CANTEEN_FULL == item)
            itemStack.change1To(Item.FILTERED_CANTEEN_EMPTY, player);
    }
}