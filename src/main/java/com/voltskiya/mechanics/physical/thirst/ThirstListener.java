package com.voltskiya.mechanics.physical.thirst;

import com.voltskiya.mechanics.VoltskiyaPlugin;
import com.voltskiya.mechanics.physical.player.PhysicalPlayerManager;
import com.voltskiya.mechanics.physical.thirst.config.ThirstConfig;
import com.voltskiya.mechanics.physical.thirst.config.item.ConsumableThirstConfig;
import com.voltskiya.mechanics.physical.thirst.item.ThirstKeys;
import java.util.HashMap;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

public class ThirstListener implements Listener {

    public static final double MAX_REACH_DISTANCE = 5.0D;

    public ThirstListener() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    public static boolean isClickingWater(Player player) {
        Location location = player.getEyeLocation();
        RayTraceResult raytrace = location.getWorld()
            .rayTraceBlocks(location, location.getDirection(), MAX_REACH_DISTANCE, FluidCollisionMode.SOURCE_ONLY, true);
        if (raytrace == null) return false;
        Block block = raytrace.getHitBlock();
        if (block == null) return false;
        return block.getType() == Material.WATER;
    }

    @EventHandler
    public void onCauldron(CauldronLevelChangeEvent e) {
        // TODO fill bottle with appropriate amount of clean water
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack consumed = event.getItem();
        ConsumableThirstConfig config = ThirstConfig.get().fromItem(consumed);
        if (config == null) return;

        Player player = event.getPlayer();

        int consumeAmount = config.getConsumeAmount();
        boolean isDirty = ThirstKeys.isDirty(consumed);
        if (isDirty) {
            player.addPotionEffects(ThirstConfig.get().getDirtyEffects());
            consumeAmount /= 2;
        }

        PhysicalPlayerManager.getPlayer(player).getThirst().drink(consumeAmount);
        int usesCount = ThirstKeys.getUses(consumed);
        ItemStack replacement = consumed.clone();
        if (usesCount > 1) {
            int usesLeft = usesCount - 1;
            config.getThirstItem().toItemStack(replacement, isDirty, usesLeft);
            event.setReplacement(replacement);
        } else if (usesCount == 1) {
            config.empty(replacement);
            event.setReplacement(replacement);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClickWater(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        ConsumableThirstConfig config = ThirstConfig.get().fromItem(item);
        if (config == null || item == null) return;

        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
            config.empty(item);
        } else if (action.isRightClick() && isClickingWater(player)) {
            boolean hasUses = ThirstKeys.getUses(item) != 0;
            boolean wrongHand = event.getHand() != EquipmentSlot.HAND;
            if (hasUses || wrongHand) {
                event.setCancelled(true);
                return;
            }
            fillContainer(item, config, true, player);
        } else return;
        event.setCancelled(true);
    }

    private void fillContainer(ItemStack item, ConsumableThirstConfig config, boolean isDirty, Player player) {
        @Nullable ItemStack overflow = config.fill(item, isDirty);
        if (overflow == null) return;
        HashMap<Integer, ItemStack> failed = player.getInventory().addItem(overflow);
        if (failed.isEmpty()) return;
        World world = player.getWorld();
        Location location = player.getLocation();
        failed.values().forEach(drop -> world.dropItemNaturally(location, drop));
    }

}