package com.voltskiya.mechanics.thirst;

import com.voltskiya.mechanics.Item;
import com.voltskiya.mechanics.PlayerHud;
import com.voltskiya.mechanics.VoltskiyaItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class ThirstListener implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        new ConsumableItemStack(e.getItem()).consumeUse(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ThirstyPlayer.join(player);
        PlayerHud.watchAir(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        ThirstyPlayer.getPlayer(e.getPlayer()).leave();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        ThirstyPlayer.reset(e.getPlayer());
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent e) {
        ThirstyPlayer.getPlayer(e.getPlayer()).onSprint();
    }

    @EventHandler
    public void onCLickWater(PlayerInteractEvent e) {
        Action action = e.getAction();
        if(action == Action.LEFT_CLICK_AIR) {
            ConsumableItemStack itemStack = new ConsumableItemStack(e.getItem());
            if (!itemStack.isConsumable())
                return;
            Item item = itemStack.getItem();
            if(item == Item.BOTTLE_DIRTY) {
                itemStack.getItemStack().setType(Material.GLASS_BOTTLE);
                return;
            }
            if(item == Item.SIMPLE_BOTTLE_DIRTY || item == Item.SIMPLE_BOTTLE_FULL) {
                itemStack.changeTo(Item.SIMPLE_BOTTLE_EMPTY, e.getPlayer());
                return;
            }
            if(item == Item.CANTEEN_DIRTY || item == Item.CANTEEN_FULL) {
                itemStack.changeTo(Item.CANTEEN_EMPTY, e.getPlayer());
                return;
            }
            if(item == Item.FILTERED_CANTEEN_FULL) {
                itemStack.changeTo(Item.FILTERED_CANTEEN_EMPTY, e.getPlayer());
                return;
            }
            return;
        }
        if (action != Action.RIGHT_CLICK_BLOCK)
            return;
        Block clickedBlock = e.getClickedBlock();
        assert clickedBlock != null;
        if (clickedBlock.getType() != Material.WATER)
            return;
        ItemStack itemStack = e.getItem();
        if (itemStack == null)
            return;
        VoltskiyaItemStack voltskiyaItemStack = new VoltskiyaItemStack(itemStack);
        if (itemStack.getType() == Material.GLASS_BOTTLE) {
            voltskiyaItemStack.changeTo(Item.BOTTLE_DIRTY, e.getPlayer());
            return;
        }
        Item item = voltskiyaItemStack.getItem();
        if (item == Item.CANTEEN_EMPTY) {
            voltskiyaItemStack.changeTo(Item.CANTEEN_DIRTY, e.getPlayer());
            return;
        }
        if (item == Item.SIMPLE_BOTTLE_EMPTY) {
            voltskiyaItemStack.changeTo(Item.SIMPLE_BOTTLE_DIRTY, e.getPlayer());
            return;
        }

        if (item == Item.FILTERED_CANTEEN_EMPTY) {
            voltskiyaItemStack.changeTo(Item.FILTERED_CANTEEN_FULL, e.getPlayer());
        }
    }
}
