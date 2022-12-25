package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import com.voltskiya.mechanics.player.VoltskiyaPlayerManager;
import com.voltskiya.mechanics.VoltskiyaRecipeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class ThirstListener implements Listener {

    public static final double MAX_REACH_DISTANCE = 5.0D;

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        ConsumableItemStack consumable = new ConsumableItemStack(e.getItem());
        ItemStack replacement = consumable.consumeUse(e.getPlayer(), e.getReplacement());
        e.setReplacement(replacement);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.discoverRecipes(VoltskiyaRecipeManager.getRecipes());
        VoltskiyaPlayerManager.getPlayer(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        VoltskiyaPlayerManager.getPlayer(e.getPlayer()).leave();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        VoltskiyaPlayerManager.getPlayer(e.getPlayer()).reset();
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent e) {
        VoltskiyaPlayerManager.getPlayer(e.getPlayer()).onSprint();
    }

    @EventHandler
    public void onCauldron(CauldronLevelChangeEvent e) {
        // todo fill bottle with appropriate amount of clean water
    }

    @EventHandler
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        GameMode newGameMode = e.getNewGameMode();
        if (GameMode.SURVIVAL != newGameMode)
            e.getPlayer().sendActionBar(Component.empty());
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
            .rayTraceBlocks(location, location.getDirection(), MAX_REACH_DISTANCE, FluidCollisionMode.SOURCE_ONLY, false);
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
            voltskiyaItemStack.changeTo(Item.BOTTLE_DIRTY, player);
            return true;
        }
        switch (item) {
            case CANTEEN_EMPTY:
                voltskiyaItemStack.changeTo(Item.CANTEEN_DIRTY, player);
                return true;
            case SIMPLE_BOTTLE_EMPTY:
                voltskiyaItemStack.changeTo(Item.SIMPLE_BOTTLE_DIRTY, player);
                return true;
            case FILTERED_CANTEEN_EMPTY:
                voltskiyaItemStack.changeTo(Item.FILTERED_CANTEEN_FULL, player);
                return true;
            default:
                return false;
        }
    }

    private void emptyContainer(ItemStack bukkitItem, Player player) {
        ConsumableItemStack itemStack = new ConsumableItemStack(bukkitItem);
        if (Material.POTION != itemStack.getItemStack().getType())
            return;
        Item item = itemStack.getItem();
        if (Item.BOTTLE_DIRTY == item) {
            itemStack.getItemStack().setType(Material.GLASS_BOTTLE);
            return;
        }
        if (Item.SIMPLE_BOTTLE_DIRTY == item || Item.SIMPLE_BOTTLE_FULL == item) {
            itemStack.changeTo(Item.SIMPLE_BOTTLE_EMPTY, player);
            return;
        }
        if (Item.CANTEEN_DIRTY == item || Item.CANTEEN_FULL == item) {
            itemStack.changeTo(Item.CANTEEN_EMPTY, player);
            return;
        }
        if (Item.FILTERED_CANTEEN_FULL == item)
            itemStack.changeTo(Item.FILTERED_CANTEEN_EMPTY, player);

    }
}