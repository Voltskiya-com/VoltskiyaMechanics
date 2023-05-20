package com.voltskiya.mechanics.food.service;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.food.config.CoolerConfig;
import com.voltskiya.mechanics.food.config.FoodRotConfig;
import com.voltskiya.mechanics.food.util.CoolerHandler;
import com.voltskiya.mechanics.food.util.FoodHandler;
import com.voltskiya.mechanics.food.util.FoodItem;
import com.voltskiya.mechanics.food.util.merge.RottingMerge;
import com.voltskiya.mechanics.food.util.merge.RottingShiftMoveResult;
import com.voltskiya.mechanics.rotting.ContainerRottingDecrement;
import com.voltskiya.mechanics.rotting.PlayerInvRottingDecrement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RottingDecrement implements FoodHandler, CoolerHandler {

    public static final Set<RottingDecrement> openInventories = new HashSet<>();
    public static final Set<PlayerInvRottingDecrement> playerInventories = new HashSet<>();

    protected final Inventory inventory;
    protected final CoolerConfig config;

    public RottingDecrement(Inventory inventory, @Nullable CoolerConfig config) {
        this.inventory = inventory;
        this.config = getCoolerConfigOrFallback(inventory);
    }

    public static void start() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(VoltskiyaPlugin.get(), RottingDecrement::tickContainers, 0, 10);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(VoltskiyaPlugin.get(), RottingDecrement::tickPlayers, 0, 10);
    }

    private static void tickContainers() {
        synchronized (openInventories) {
            openInventories.removeIf(RottingDecrement::shouldRemove);
        }
        openInventories.forEach(RottingDecrement::tick);
    }

    private static void tickPlayers() {
        synchronized (playerInventories) {
            playerInventories.removeIf(RottingDecrement::shouldRemove);
            if (playerInventories.size() != Bukkit.getOnlinePlayers().size()) {
                Set<Player> playersToAdd = new HashSet<>(Bukkit.getOnlinePlayers());
                for (PlayerInvRottingDecrement inv : playerInventories) {
                    playersToAdd.remove(inv.getPlayer());
                }
                playersToAdd.forEach(RottingDecrement::addPlayer);
            }
        }
        playerInventories.forEach(RottingDecrement::tick);
    }

    public static void addInventory(Inventory inventory) {
        RottingDecrement decrement = new ContainerRottingDecrement(inventory);
        decrement.tick();
        openInventories.add(decrement);
    }

    public static void addPlayer(Player player) {
        playerInventories.add(new PlayerInvRottingDecrement(player));
    }

    public void tick() {
        initTick();
        List<ItemStack> addDecayInto = new ArrayList<>(0);
        @Nullable ItemStack @NotNull [] contents = inventory.getContents();
        for (int slot = 0, length = contents.length; slot < length; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType().isAir())
                continue;
            if (shouldSkipItem(slot)) return;
            if (FoodRotConfig.get().hasFoodTimer(item.getType())) {
                ItemStack rot = new FoodItem(item, config).update().getRot();
                if (rot == null) continue;
                addDecayInto.add(rot);
            }
        }
        for (ItemStack decay : addDecayInto) {
            RottingShiftMoveResult result = RottingMerge.shiftMove(inventory, decay);
            if (result != RottingShiftMoveResult.COMPLETE) {
                addOverflowItem(decay);
            }
        }
    }

    protected void initTick() {
    }

    protected boolean shouldSkipItem(int slot) {
        return false;
    }

    protected void addOverflowItem(ItemStack decay) {

    }

    protected abstract boolean shouldRemove();
}
