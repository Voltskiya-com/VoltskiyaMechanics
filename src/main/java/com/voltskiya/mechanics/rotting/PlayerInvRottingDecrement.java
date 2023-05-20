package com.voltskiya.mechanics.rotting;

import com.voltskiya.mechanics.food.config.CoolerConfigDatabase;
import com.voltskiya.mechanics.food.service.RottingDecrement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInvRottingDecrement extends RottingDecrement {

    private static final long SKIP_MAIN_HAND_CHECK = 60_000L;
    protected final Player player;
    private long lastMainHandUpdate = 0;
    private boolean shouldPassCheck = false;
    private int mainHandSlot;

    public PlayerInvRottingDecrement(Player player) {
        super(player.getInventory(), CoolerConfigDatabase.get().getPlayerConfig());
        this.player = player;
    }

    @Override
    protected boolean shouldRemove() {
        return !player.isOnline();
    }

    @Override
    protected void initTick() {
        // if we change hands
        if (didChangeHands()) {
            setLastCheckNow();
            this.mainHandSlot = player.getInventory().getHeldItemSlot();
            this.shouldPassCheck = true;
        } else {
            this.shouldPassCheck = System.currentTimeMillis() - lastMainHandUpdate < SKIP_MAIN_HAND_CHECK;
            if (!this.shouldPassCheck)
                setLastCheckNow();
        }
    }

    private void setLastCheckNow() {
        this.lastMainHandUpdate = System.currentTimeMillis();
    }

    private boolean didChangeHands() {
        return player.getInventory().getHeldItemSlot() != this.mainHandSlot;
    }

    @Override
    protected boolean shouldSkipItem(int slot) {
        // are they the same reference?

        return this.shouldPassCheck && slot == this.mainHandSlot;
    }

    @Override
    protected void addOverflowItem(ItemStack decay) {
        player.getWorld().dropItem(player.getLocation(), decay);
    }

    public Player getPlayer() {
        return this.player;
    }
}
