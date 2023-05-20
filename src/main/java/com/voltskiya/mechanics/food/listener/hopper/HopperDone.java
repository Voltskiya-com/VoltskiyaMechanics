package com.voltskiya.mechanics.food.listener.hopper;

import com.voltskiya.mechanics.food.config.FoodRotConfig;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class HopperDone {

    public boolean hopperDone;
    public List<InventoryMoveItemEvent> firstWaveCheckEvents = new ArrayList<>();
    public List<InventoryMoveItemEvent> secondWaveMergeEvents = new ArrayList<>();


    public HopperDone(InventoryMoveItemEvent event) {
        hopperDone = false;
        this.firstWaveCheckEvents.add(event);
        this.secondWaveMergeEvents.add(event);
    }

    public void add(InventoryMoveItemEvent event) {
        hopperDone = false;
        this.firstWaveCheckEvents.add(event);
        if (FoodRotConfig.get().hasFoodTimer(event.getItem().getType()))
            this.secondWaveMergeEvents.add(event);
    }

    public void clear() {
        firstWaveCheckEvents = new ArrayList<>();
        secondWaveMergeEvents = new ArrayList<>();
    }

    public void clearFirst() {
        firstWaveCheckEvents = new ArrayList<>();
    }
}
